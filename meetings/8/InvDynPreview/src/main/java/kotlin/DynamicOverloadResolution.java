package kotlin;


import kotlin.builtins.*;
import kotlin.text.StringsKt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static kotlin.jvm.JvmClassMappingKt.getJavaObjectType;
import static kotlin.jvm.JvmClassMappingKt.getKotlinClass;

public final class DynamicOverloadResolution {
    /* package */ static final String DEFAULT_CALLER_SUFFIX = "$default";
    private static final Map<Class, Class> BUILTIN_CLASSES = new HashMap<>();

    static {
        BUILTIN_CLASSES.put(java.lang.Byte.class, ByteBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.Double.class, DoubleBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.Float.class, FloatBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.Integer.class, IntBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.Long.class, LongBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.Short.class, ShortBuiltins.class);
        BUILTIN_CLASSES.put(java.lang.String.class, StringBuiltins.class);
        BUILTIN_CLASSES.put(java.util.ArrayList.class, ListBuiltins.class);
        BUILTIN_CLASSES.put(boolean[].class, BooleanArrayBuiltins.class);
        BUILTIN_CLASSES.put(byte[].class, ByteArrayBuiltins.class);
        BUILTIN_CLASSES.put(char[].class, CharArrayBuiltins.class);
        BUILTIN_CLASSES.put(double[].class, DoubleArrayBuiltins.class);
        BUILTIN_CLASSES.put(float[].class, FloatArrayBuiltins.class);
        BUILTIN_CLASSES.put(int[].class, IntArrayBuiltins.class);
        BUILTIN_CLASSES.put(long[].class, LongArrayBuiltins.class);
        BUILTIN_CLASSES.put(short[].class, ShortArrayBuiltins.class);
    }

    private static List<Method> fastMethodFilter(List<Method> methods, String name) {
        String defaultName = name + DEFAULT_CALLER_SUFFIX;
        return methods.stream()
                .filter(it -> (it.getName().equals(name) && !it.isBridge()) || it.getName().equals(defaultName))
                .distinct()
                .collect(Collectors.toList());
    }

    private static List<Method> filterSuitableMethods(List<Method> methods, Object[] arguments) {
        return filterSuitableMethods(methods, arguments, false);
    }

    private static List<Method> filterSuitableMethods(List<Method> methods, Object[] arguments, boolean skipReceiverCheck) {
        return methods.stream().filter(it -> DynamicUtilsKt.isMethodSuitable(it, arguments, skipReceiverCheck)).collect(Collectors.toList());
    }

    private static List<Method> findBuiltins(String name, Class methodClass) {
        Class builtinClass = BUILTIN_CLASSES.get(methodClass);
        if (builtinClass == null) {
            return Collections.emptyList();
        }
        return fastMethodFilter(Arrays.asList(builtinClass.getDeclaredMethods()), name);
    }

    private static boolean isBridgeForMethod(Method bridge, Method candidateMethod) {
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

    private static Method findMostSpecific(List<Method> methods) {
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

    private static Class<?> prepareClassForCompare(Class<?> clazz) {
        return getJavaObjectType(getKotlinClass(clazz));
    }

    /* package */
    static DynamicSelector.TypeCompareResult isTypeMoreSpecific(Class<?> a, Class<?> b) {
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

    private static Class<?> getTypeParameter(Class<?>[] aParameters, int index, boolean isVarargs) {
        if (!isVarargs || (index < aParameters.length - 1)) {
            return aParameters[index];
        }
        return aParameters[aParameters.length - 1].getComponentType();
    }

    private static boolean isMoreSpecific(Method a, Method b) {
        if (a == b)
            return true;
        if (a.getName().endsWith(DEFAULT_CALLER_SUFFIX))
            return true;
        if (b.getName().endsWith(DEFAULT_CALLER_SUFFIX))
            return false;

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
        boolean isAVarargs = a.isVarArgs();
        boolean isBVarargs = b.isVarArgs();

        int minimumCompareResult = DynamicSelector.TypeCompareResult.WORSE.index;
        final int lastIndex = Math.min(aParameters.length, bParameters.length);
        for (int i = 0; i < lastIndex; ++i) {
            DynamicSelector.TypeCompareResult compareResult = isTypeMoreSpecific(
                    getTypeParameter(aParameters, i, isAVarargs),
                    getTypeParameter(bParameters, i, isBVarargs));
            if (compareResult == DynamicSelector.TypeCompareResult.WORSE) {
                return false;
            }
            minimumCompareResult = Math.min(minimumCompareResult, compareResult.index);
        }

        boolean compareResult = minimumCompareResult <= DynamicSelector.TypeCompareResult.BETTER.index;

        if (!compareResult) {
            if (!isAVarargs && isBVarargs)
                return true;
        }
        return compareResult;
    }

    private static boolean isMoreSpecificThenAllOf(Method candidate, Collection<Method> descriptors) {
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

    /* Nullable */
    private static MethodHandle resolveField(MethodHandles.Lookup caller,
                                             String name,
                                             Object[] arguments,
                                             boolean isGetter) {
        Object receiver = arguments[0];
        if (receiver == null) {
            throw new NullPointerException("Unsupported receiver - null");
        } else if (receiver instanceof Class) {
            throw new UnsupportedOperationException("Static receiver");
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

    /* Nullable */
    /* package */
    static MethodHandle resolveFieldOrPropertyGetter(MethodHandles.Lookup caller,
                                                     String name,
                                                     Object[] arguments,
                                                     boolean isStaticCall) {
        MethodHandle handle = resolveField(caller, name, arguments, /* isGetter */true);
        if (handle == null) {
            name = DynamicMetafactory.InvokeType.GET.getJavaPrefix() + StringsKt.capitalize(name);
            return resolveMethod(caller, name, arguments, null, isStaticCall);
        }
        return handle;
    }

    /* Nullable */
    /* package */
    static MethodHandle resolveFieldOrPropertySetter(MethodHandles.Lookup caller,
                                                     String name,
                                                     Object[] arguments,
                                                     boolean isStaticCall) {
        MethodHandle handle = resolveField(caller, name, arguments, /* isGetter */false);
        if (handle == null) {
            name = DynamicMetafactory.InvokeType.SET.getJavaPrefix() + StringsKt.capitalize(name);
            return resolveMethod(caller, name, arguments, null, isStaticCall);
        }
        return handle;
    }

    /* Nullable */
    /* package */
    static MethodHandle resolveMethod(MethodHandles.Lookup caller,
                                      String name,
                                      Object[] arguments,
                                      /* Nullable */ String[] namedArguments,
                                      boolean isStaticCall) {
        Object receiver = arguments[0];
        if (receiver == null) {
            throw new NullPointerException("Unsupported receiver - null");
        } else {
            Class methodClass;
            if (isStaticCall) {
                methodClass = (Class) receiver;
            } else {
                methodClass = receiver.getClass();
            }
            return resolveMethodAndFindClass(caller, name, arguments, namedArguments, methodClass);
        }
    }

    private static boolean checkAccess(MethodHandles.Lookup caller, Method method) {
        if (Modifier.isPrivate(method.getModifiers()) &&
                !(method.getClass().getPackage() == caller.getClass().getPackage())) {
            return true;
        }
        try {
            caller.unreflect(method);
            return true;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    /* Nullable */
    private static MethodHandle resolveMethodAndFindClass(MethodHandles.Lookup caller,
                                                          String name,
                                                          Object[] arguments,
                                                          /* Nullable */ String[] namedArguments,
                                                          Class methodClass) {
        MethodHandle handle;
        try {
            handle = resolveMethodHandleOnClass(caller, name, arguments, namedArguments, methodClass);
            return handle;
        } catch (IllegalAccessException e) {
            // IllegalAccessException was iff we on right hierarchy path. We continue search accessible method.
        }

        List<Class> currentLayer = Collections.singletonList(methodClass);
        List<Method> methodHandles = new ArrayList<>();
        while (!currentLayer.isEmpty()) {
            List<Class> nextLayer = new ArrayList<>();

            for (Class cls : currentLayer) {
                Collections.addAll(nextLayer, cls.getInterfaces());
                nextLayer.add(cls.getSuperclass());
            }

            currentLayer = nextLayer.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());

            methodHandles.clear();
            for (Class cls : currentLayer) {
                Method currentMethod = resolveMethodOnClass(caller, name, arguments, namedArguments, cls);
                if ((currentMethod != null) && checkAccess(caller, currentMethod)) {
                    methodHandles.add(currentMethod);
                }
            }

            Method method = findMostSpecific(methodHandles);
            if (method != null) {
                try {
                    return transformMethodToMethodHandle(caller, method, name, arguments, namedArguments, methodClass);
                } catch (IllegalAccessException e) {
                    return null;
                }
            }
        }

        return null;
    }

    private static MethodHandle transformMethodToMethodHandle(MethodHandles.Lookup caller,
                                                              Method targetMethod,
                                                              String name,
                                                              Object[] arguments,
                                                            /* Nullable */ String[] namedArguments,
                                                              Class methodClass) throws IllegalAccessException {
        Method owner = null;
        boolean requireOwner = namedArguments != null && namedArguments.length > 0;
        if (requireOwner) {
            List<Method> methods = Arrays.asList(methodClass.getMethods());
            methods = fastMethodFilter(methods, name);
            owner = resolveBridgeOwner(targetMethod, methods);
        }

        MethodHandle handle = caller.unreflect(targetMethod);
        handle = DynamicUtilsKt.insertDefaultArgumentsAndNamedParameters(handle, targetMethod, owner, namedArguments, arguments);

        return handle;
    }

    /* Nullable */
    private static MethodHandle resolveMethodHandleOnClass(MethodHandles.Lookup caller,
                                                           String name,
                                                           Object[] arguments,
                                                    /* Nullable */ String[] namedArguments,
                                                           Class methodClass) throws IllegalAccessException {
        Method targetMethod = resolveMethodOnClass(caller, name, arguments, namedArguments, methodClass);
        if (targetMethod == null) {
            return null;
        }
        return transformMethodToMethodHandle(caller, targetMethod, name, arguments, namedArguments, methodClass);
    }

    /* Nullable */
    private static Method resolveMethodOnClass(MethodHandles.Lookup caller,
                                               String name,
                                               Object[] arguments,
                                                    /* Nullable */ String[] namedArguments,
                                               Class methodClass) {
        List<Method> methods = Arrays.asList(methodClass.getMethods());

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

        return targetMethod;
    }
}
