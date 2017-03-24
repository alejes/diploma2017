package kotlin;


import kotlin.builtins.*;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static kotlin.jvm.JvmClassMappingKt.getJavaObjectType;
import static kotlin.jvm.JvmClassMappingKt.getKotlinClass;

public class DynamicOverloadResolution {
    /* package */ static final String DEFAULT_CALLER_SUFFIX = "$default";
    @NotNull
    private static final Map<Class, Class> BUILTIN_CLASSES = new HashMap<>();

    static {
        BUILTIN_CLASSES.put(java.lang.Byte.class, ByteBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.Double.class, DoubleBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.Float.class, FloatBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.Integer.class, IntBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.Long.class, LongBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.Short.class, ShortBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.String.class, StringBuiltins.class);
        BUILTIN_CLASSES.put(boolean[].class, BooleanArrayBuiltins.class);
        BUILTIN_CLASSES.put(byte[].class, ByteArrayBuiltins.class);
        BUILTIN_CLASSES.put(char[].class, CharArrayBuiltins.class);
        BUILTIN_CLASSES.put(double[].class, DoubleArrayBuiltins.class);
        BUILTIN_CLASSES.put(float[].class, FloatArrayBuiltins.class);
        BUILTIN_CLASSES.put(int[].class, IntArrayBuiltins.class);
        BUILTIN_CLASSES.put(long[].class, LongArrayBuiltins.class);
        BUILTIN_CLASSES.put(short[].class, ShortArrayBuiltins.class);
    }

    @NotNull
    private static List<Method> fastMethodFilter(@NotNull List<Method> methods, @NotNull String name) {
        String defaultName = name + DEFAULT_CALLER_SUFFIX;
        return methods.stream()
                .filter(it -> (it.getName().equals(name) && !it.isBridge()) || it.getName().equals(defaultName))
                .distinct()
                .collect(Collectors.toList());
    }

    @NotNull
    private static List<Method> filterSuitableMethods(@NotNull List<Method> methods, @NotNull Object[] arguments) {
        return filterSuitableMethods(methods, arguments, false);
    }

    @NotNull
    private static List<Method> filterSuitableMethods(@NotNull List<Method> methods, @NotNull Object[] arguments, boolean skipReceiverCheck) {
        return methods.stream().filter(it -> DynamicUtilsKt.isMethodSuitable(it, arguments, skipReceiverCheck)).collect(Collectors.toList());
    }

    @NotNull
    private static List<Method> findBuiltins(@NotNull String name, @NotNull Class methodClass) {
        Class builtinClass = BUILTIN_CLASSES.get(methodClass);
        if (builtinClass == null) {
            return Collections.emptyList();
        }
        return fastMethodFilter(Arrays.asList(builtinClass.getDeclaredMethods()), name);
    }

    private static boolean isBridgeForMethod(@NotNull Method bridge, @NotNull Method candidateMethod) {
        Class<?>[] parameterTypes = bridge.getParameterTypes();
        if (!parameterTypes[0].equals(candidateMethod.getDeclaringClass()))
            return false;

        int index = 1;
        for (Class<?> candidateType : candidateMethod.getParameterTypes()) {
            if (!candidateType.equals(parameterTypes[index]))
                return false;
            ++index;
        }
        return true;
    }

    private static Method findMostSpecific(@NotNull List<Method> methods) {
        if (methods.isEmpty()) {
            return null;
        } else if (methods.size() == 1) {
            return methods.get(0);
        }

        for (Method method : methods) {
            if (isMoreSpecificThenAllOf(method, methods)) {
                return method;
            }
        }

        return null;
    }

    private static Class<?> prepareClassForCompare(@NotNull Class<?> clazz) {
        return getJavaObjectType(getKotlinClass(clazz));
    }

    /* package */
    static DynamicSelector.TypeCompareResult isTypeMoreSpecific(@NotNull Class<?> a, @NotNull Class<?> b) {
        if (a == b) {
            return DynamicSelector.TypeCompareResult.EQUAL;
        }
        Class<?> first = prepareClassForCompare(a);
        Class<?> second = prepareClassForCompare(b);
        if (first == second) {
            return DynamicSelector.TypeCompareResult.BOXING;
        }

        if (second.isAssignableFrom(first)) {
            return DynamicSelector.TypeCompareResult.BETTER;
        } else {
            return DynamicSelector.TypeCompareResult.WORSE;
        }
    }

    private static boolean isMoreSpecific(@NotNull Method a, @NotNull Method b) {
        if (a == b)
            return true;
        if (a.getName().endsWith(DEFAULT_CALLER_SUFFIX))
            return false;
        if (b.getName().endsWith(DEFAULT_CALLER_SUFFIX))
            return true;

        switch (isTypeMoreSpecific(a.getReturnType(), b.getReturnType())) {
            case BETTER:
                return true;
            case WORSE:
                return false;
        }


            /*
             * [TODO] isVisibilityMoreSpecific
             */
        Class<?>[] aParameters = a.getParameterTypes();
        Class<?>[] bParameters = b.getParameterTypes();

        int minimumCompareResult = DynamicSelector.TypeCompareResult.WORSE.index;
        for (int i = 0; i < aParameters.length; ++i) {
            DynamicSelector.TypeCompareResult compareResult = isTypeMoreSpecific(aParameters[i], bParameters[i]);
            if (compareResult == DynamicSelector.TypeCompareResult.WORSE) {
                return false;
            }
            minimumCompareResult = Math.min(minimumCompareResult, compareResult.index);
        }

        return minimumCompareResult <= DynamicSelector.TypeCompareResult.BETTER.index;
    }

    private static boolean isMoreSpecificThenAllOf(@NotNull Method candidate, @NotNull Collection<Method> descriptors) {
        // NB subtyping relation in Kotlin is not transitive in presence of flexible types:
        //  String? <: String! <: String, but not String? <: String
        for (Method descriptor : descriptors) {
            if (!isMoreSpecific(candidate, descriptor)) {
                return false;
            }
        }
        return true;
    }

    private static Method resolveBridgeOwner(Method targetMethod, List<Method> listForSearchOriginalForBridge) {
        Method method = targetMethod;
        if (targetMethod.isBridge()) {
            Optional<Method> candidate = listForSearchOriginalForBridge.stream()
                    .filter(it -> !it.isBridge() && (it != targetMethod) && !it.getName().endsWith(DEFAULT_CALLER_SUFFIX))
                    .filter(it -> isBridgeForMethod(targetMethod, it)).findFirst();
            if (!candidate.isPresent()) {
                return null;
            }
            method = candidate.get();
        }
        return method;
    }

    @Nullable
    private static MethodHandle resolveField(@NotNull MethodHandles.Lookup caller,
                                             @NotNull String name,
                                             @NotNull Object[] arguments,
                                             boolean isGetter) {
        Object receiver = arguments[0];
        if (receiver == null) {
            throw new UnsupportedOperationException("null");
        } else if (receiver instanceof Class) {
            throw new UnsupportedOperationException("static");
        } else {
            try {
                Field field = receiver.getClass().getDeclaredField(name);
                if (isGetter) {
                    return caller.unreflectGetter(field);
                } else {
                    return caller.unreflectSetter(field);
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                return null;
            }
        }
    }

    @Nullable
    /* package */ static MethodHandle resolveFieldOrPropertyGetter(@NotNull MethodHandles.Lookup caller,
                                                                   @NotNull String name,
                                                                   @NotNull Object[] arguments,
                                                                   boolean isStaticCall) {
        MethodHandle handle = resolveField(caller, name, arguments, /* isGetter */true);
        if (handle == null) {
            name = DynamicMetaFactory.InvokeType.GET.getJavaPrefix() + StringsKt.capitalize(name);
            return resolveMethod(caller, name, arguments, null, isStaticCall);
        }
        return handle;
    }

    @Nullable
    /* package */ static MethodHandle resolveFieldOrPropertySetter(@NotNull MethodHandles.Lookup caller,
                                                                   @NotNull String name,
                                                                   @NotNull Object[] arguments,
                                                                   boolean isStaticCall) {
        MethodHandle handle = resolveField(caller, name, arguments, /* isGetter */false);
        if (handle == null) {
            name = DynamicMetaFactory.InvokeType.SET.getJavaPrefix() + StringsKt.capitalize(name);
            return resolveMethod(caller, name, arguments, null, isStaticCall);
        }
        return handle;
    }

    @Nullable
    /* package */ static MethodHandle resolveMethod(@NotNull MethodHandles.Lookup caller,
                                                    @NotNull String name,
                                                    @NotNull Object[] arguments,
                                                    @Nullable String[] namedArguments,
                                                    boolean isStaticCall) {
        Object receiver = arguments[0];
        if (receiver == null) {
            throw new NullPointerException("Unsupported receiver - null");
        } else {
            Class methodClass;
            MethodHandle handle;
            if (isStaticCall) {
                methodClass = (Class) receiver;
            } else {
                methodClass = receiver.getClass();
            }
            List<Method> methods = new ArrayList<>(Arrays.asList(methodClass.getDeclaredMethods()));
            Collections.addAll(methods, receiver.getClass().getMethods());

            methods = fastMethodFilter(methods, name);

            List<Method> targetMethodList = filterSuitableMethods(methods, arguments);

            boolean isMixedWithBuiltins = targetMethodList.isEmpty();
            if (isMixedWithBuiltins) {
                targetMethodList = filterSuitableMethods(findBuiltins(name, methodClass), arguments, true);
            }

            Method targetMethod = findMostSpecific(targetMethodList);

            // since we have Int.compareTo(Long) together with Integer.compareTo(Integer) and similar,
            // we must mix with builtins if failed
            if ((targetMethod == null) && !isMixedWithBuiltins) {
                targetMethodList = filterSuitableMethods(findBuiltins(name, methodClass), arguments, true);
                targetMethod = findMostSpecific(targetMethodList);
            }
            if (targetMethod == null) {
                return null;
            }

            Method owner = null;
            boolean requireOwner = namedArguments != null && namedArguments.length > 0;
            if (requireOwner) {
                owner = resolveBridgeOwner(targetMethod, methods);
            }

            try {
                handle = caller.unreflect(targetMethod);
            } catch (IllegalAccessException e) {
                throw new DynamicBindException(e.getMessage());
            }
            handle = DynamicUtilsKt.insertDefaultArgumentsAndNamedParameters(handle, targetMethod, owner, namedArguments, arguments);

            return handle;
        }
    }
}
