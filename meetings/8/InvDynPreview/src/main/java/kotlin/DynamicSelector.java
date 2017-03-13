package kotlin;

import kotlin.builtins.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static kotlin.DynamicMetaFactory.IS_INSTANCE;
import static kotlin.jvm.JvmClassMappingKt.getJavaObjectType;
import static kotlin.jvm.JvmClassMappingKt.getKotlinClass;

public abstract class DynamicSelector {
    protected static final String DEFAULT_CALLER_SUFFIX = "$default";
    protected final Object[] arguments;
    protected final MethodHandles.Lookup caller;
    protected final MutableCallSite mc;
    protected final MethodType type;
    protected final boolean isStaticCall;
    protected String name;
    protected MethodHandle handle;

    private DynamicSelector(Object[] arguments, MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, boolean isStaticCall) {
        this.arguments = arguments;
        this.mc = mc;
        this.caller = caller;
        this.type = type;
        this.name = name;
        this.isStaticCall = isStaticCall;
    }

    /* package */ static DynamicSelector getFieldSelector(MutableCallSite mc,
                                              MethodHandles.Lookup caller,
                                              MethodType type,
                                              String name,
                                              Object[] arguments,
                                              DynamicMetaFactory.INVOKE_TYPE it) {
        return new FieldSelector(mc, caller, type, name, arguments, it);
    }

    /* package */ static DynamicSelector getMethodSelector(MutableCallSite mc,
                                            MethodHandles.Lookup caller,
                                            MethodType type,
                                            String name,
                                            Object[] arguments,
                                            @Nullable String[] namedArguments) {
        return new MethodSelector(mc, caller, type, name, arguments, namedArguments);
    }

    /* package */ static TypeCompareResult isTypeMoreSpecific(@NotNull Class<?> a, @NotNull Class<?> b) {
        if (a == b) {
            return TypeCompareResult.EQUAL;
        }
        Class<?> first = prepareClassForCompare(a);
        Class<?> second = prepareClassForCompare(b);
        if (first == second) {
            return TypeCompareResult.BOXING;
        }

        if (second.isAssignableFrom(first)) {
            return TypeCompareResult.BETTER;
        } else {
            return TypeCompareResult.WORSE;
        }
    }

    private static Class<?> prepareClassForCompare(Class<?> clazz) {
        return getJavaObjectType(getKotlinClass(clazz));
    }

    /* package */ abstract boolean setCallSite() throws DynamicBindException;

    /* package */ void changeName(String name) {
        this.name = name;
    }

    /* package */ MethodHandle getMethodHandle() {
        return handle;
    }

    /* package */ enum TypeCompareResult {
        BETTER(0),
        EQUAL(1),
        BOXING(2),
        WORSE(3);
        int index;

        TypeCompareResult(int index) {
            this.index = index;
        }

    }

    private static class MethodSelector extends DynamicSelector {
        @NotNull
        private static final Map<Class, Class> BUILTIN_CLASSES = new HashMap<>();

        static {
            BUILTIN_CLASSES.put(java.lang.Byte.class, ByteBuiltins.class);
            BUILTIN_CLASSES.put(java.lang.Double.class, DoubleBuiltins.class);
            BUILTIN_CLASSES.put(java.lang.Float.class, FloatBuiltins.class);
            BUILTIN_CLASSES.put(java.lang.Integer.class, IntBuiltins.class);
            BUILTIN_CLASSES.put(java.lang.Long.class, LongBuiltins.class);
            BUILTIN_CLASSES.put(java.lang.Short.class, ShortBuiltins.class);
            BUILTIN_CLASSES.put(boolean[].class, BooleanArrayBuiltins.class);
            BUILTIN_CLASSES.put(byte[].class, ByteArrayBuiltins.class);
            BUILTIN_CLASSES.put(char[].class, CharArrayBuiltins.class);
            BUILTIN_CLASSES.put(double[].class, DoubleArrayBuiltins.class);
            BUILTIN_CLASSES.put(float[].class, FloatArrayBuiltins.class);
            BUILTIN_CLASSES.put(int[].class, IntArrayBuiltins.class);
            BUILTIN_CLASSES.put(long[].class, LongArrayBuiltins.class);
            BUILTIN_CLASSES.put(short[].class, ShortArrayBuiltins.class);
        }

        @Nullable
        private String[] namedArguments;
        private MethodSelector(MutableCallSite mc,
                               MethodHandles.Lookup caller,
                               MethodType type,
                               String name,
                               Object[] arguments,
                               @Nullable String[] namedArguments) {
            super(arguments, mc, caller, type, name, /* isStaticCall*/ arguments[0] instanceof Class);
            this.namedArguments = namedArguments;
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

            int minimumCompareResult = TypeCompareResult.WORSE.index;
            for (int i = 0; i < aParameters.length; ++i) {
                TypeCompareResult compareResult = isTypeMoreSpecific(aParameters[i], bParameters[i]);
                if (compareResult == TypeCompareResult.WORSE) {
                    return false;
                }
                minimumCompareResult = Math.min(minimumCompareResult, compareResult.index);
            }

            return minimumCompareResult <= TypeCompareResult.BETTER.index;
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

        @NotNull
        private static List<Method> fastMethodFilter(@NotNull List<Method> methods, String name) {
            String defaultName = name + DEFAULT_CALLER_SUFFIX;
            return methods.stream()
                    .filter(it -> (it.getName().equals(name) && !it.isBridge()) || it.getName().equals(defaultName))
                    .distinct()
                    .collect(Collectors.toList());
        }

        @Override
        /* package */ boolean setCallSite() {
            if (!genMethodClass()) {
                return false;
            }
            prepareMetaHandlers();
            changeTargetGuard();
            processSetCallSite();
            return true;
        }

        private void prepareMetaHandlers() {
            //cached in groovy
            if (isStaticCall) {
                MethodType staticType = type.dropParameterTypes(0, 1);
                handle = MethodHandles.explicitCastArguments(handle, staticType);
                handle = MethodHandles.dropArguments(handle, 0, Class.class);
            } else {
                handle = MethodHandles.explicitCastArguments(handle, type);
            }
        }

        private void changeTargetGuard() {
            MethodHandle fallback = DynamicMetaFactory.makeFallBack(mc, caller, type, name, null, DynamicMetaFactory.INVOKE_TYPE.METHOD);
            Class<?>[] handleParameters = handle.type().parameterArray();
            for (int i = 0; i < arguments.length; ++i) {
                MethodHandle guard = IS_INSTANCE
                        .bindTo(arguments[i].getClass())
                        .asType(MethodType.methodType(boolean.class, handleParameters[i]));
                //MethodHandle sub1 = MethodHandles.permuteArguments(handle, CLASS_INSTANCE_MTYPE, i);
                Class[] dropTypes = new Class[i];
                System.arraycopy(handleParameters, 0, dropTypes, 0, dropTypes.length);
                guard = MethodHandles.dropArguments(guard, 0, dropTypes);
                handle = MethodHandles.guardWithTest(guard, handle, fallback);
            }
        }

        private void processSetCallSite() {
            mc.setTarget(handle);
        }

        @NotNull
        private List<Method> filterSuitableMethods(@NotNull List<Method> methods) {
            return filterSuitableMethods(methods, false);
        }

        @NotNull
        private List<Method> filterSuitableMethods(@NotNull List<Method> methods, boolean skipReceiverCheck) {
            return methods.stream().filter(it -> DynamicUtilsKt.isMethodSuitable(it, arguments, skipReceiverCheck)).collect(Collectors.toList());
        }

        @NotNull
        private List<Method> findBuiltins(@NotNull Class methodClass) {
            Class builtinClass = BUILTIN_CLASSES.get(methodClass);
            if (builtinClass == null) {
                return Collections.emptyList();
            }
            return fastMethodFilter(Arrays.asList(builtinClass.getDeclaredMethods()), name);
        }

        private boolean isBridgeForMethod(Method bridge, Method candidateMethod) {
            Class<?>[] parameterTypes = bridge.getParameterTypes();
            if (!parameterTypes[0].equals(candidateMethod.getDeclaringClass()))
                return false;

            int index = 1;
            for (Class<?> candidateType : candidateMethod.getParameterTypes()) {
                if (!candidateType.equals(parameterTypes[index]))
                    return false;
            }
            return true;
        }

        private Method resolveBridgeOwner(Method targetMethod, List<Method> listForSearchOriginalForBridge) {
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

        private boolean genMethodClass() {
            Object receiver = arguments[0];
            if (receiver == null) {
                throw new UnsupportedOperationException("null");
            } else {
                Class methodClass;
                if (isStaticCall) {
                    methodClass = (Class) receiver;
                } else {
                    methodClass = receiver.getClass();
                }
                List<Method> methods = new ArrayList<>(Arrays.asList(methodClass.getDeclaredMethods()));
                Collections.addAll(methods, receiver.getClass().getMethods());

                methods = fastMethodFilter(methods, name);

                List<Method> targetMethodList = filterSuitableMethods(methods);

                boolean isMixedWithBuiltins = targetMethodList.isEmpty();
                if (isMixedWithBuiltins) {
                    targetMethodList = filterSuitableMethods(findBuiltins(methodClass), true);
                }

                Method targetMethod = findMostSpecific(targetMethodList);

                // since we have Int.compareTo(Long) together with Integer.compareTo(Integer) and similar,
                // we must mix with builtins if failed
                if ((targetMethod == null) && !isMixedWithBuiltins) {
                    targetMethodList = filterSuitableMethods(findBuiltins(methodClass), true);
                    targetMethod = findMostSpecific(targetMethodList);
                }
                if (targetMethod == null) {
                    return false;
                }

                Method owner = null;
                boolean requireOwner = namedArguments != null && namedArguments.length > 0;
                if (requireOwner) {
                    owner = resolveBridgeOwner(targetMethod, methods);
                }


                targetMethod.setAccessible(true);

                try {
                    handle = caller.unreflect(targetMethod);
                } catch (IllegalAccessException e) {
                    throw new DynamicBindException(e.getMessage());
                }
                handle = DynamicUtilsKt.insertDefaultArgumentsAndNamedParameters(handle, targetMethod, owner, namedArguments, arguments);

                return true;
            }
        }
    }

    private static class FieldSelector extends DynamicSelector {
        private final DynamicMetaFactory.INVOKE_TYPE it;

        private FieldSelector(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments, DynamicMetaFactory.INVOKE_TYPE it) {
            // [TODO] static call for fields
            super(arguments, mc, caller, type, name, /* isStaticCall */ false);
            this.it = it;
        }

        @Override
        /* package */  boolean setCallSite() {
            genMethodClass();
            processSetCallSite();
            return true;
        }


        private void processSetCallSite() {
            //cached in groovy
            handle = MethodHandles.explicitCastArguments(handle, type);
            mc.setTarget(handle);
        }

        private void genMethodClass() {
            Object receiver = arguments[0];
            if (receiver == null) {
                throw new UnsupportedOperationException("null");
            } else if (receiver instanceof Class) {
                throw new UnsupportedOperationException("static");
            } else {
                try {
                    Field field = receiver.getClass().getDeclaredField(name);
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    switch (it) {
                        case GET:
                            // handle = caller.findGetter(receiver.getClass(), name, ???type)
                            handle = caller.unreflectGetter(field);
                            break;
                        case SET:
                            handle = caller.unreflectSetter(field);
                            break;
                    }

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new DynamicBindException(e);
                }
            }
        }
    }

}
