package kotlin;

import jdk.nashorn.internal.ir.annotations.Immutable;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import kotlin.builtins.*;

import static kotlin.DynamicMetaFactory.IS_INSTANCE;
import static kotlin.jvm.JvmClassMappingKt.getJavaObjectType;
import static kotlin.jvm.JvmClassMappingKt.getKotlinClass;

public abstract class DynamicSelector {
    protected final String name;
    protected final Object[] arguments;
    protected final MethodHandles.Lookup caller;
    protected final MutableCallSite mc;
    protected final MethodType type;
    protected final boolean isStaticCall;
    protected MethodHandle handle;

    private DynamicSelector(Object[] arguments, MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, boolean isStaticCall) {
        this.arguments = arguments;
        this.mc = mc;
        this.caller = caller;
        this.type = type;
        this.name = name;
        this.isStaticCall = isStaticCall;
    }

    public static DynamicSelector getSelector(MutableCallSite mc,
                                              MethodHandles.Lookup caller,
                                              MethodType type,
                                              String name,
                                              Object[] arguments,
                                              DynamicMetaFactory.INVOKE_TYPE it) {
        if (it == DynamicMetaFactory.INVOKE_TYPE.METHOD) {
            return new MethodSelector(mc, caller, type, name, arguments);
        } else {
            return new FieldSelector(mc, caller, type, name, arguments, it);
        }
    }

    public abstract void setCallSite() throws DynamicBindException;

    public MethodHandle getMethodHandle() {
        return handle;
    }

    private enum TypeCompareResult {
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
        private MethodSelector(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) {
            super(arguments, mc, caller, type, name, /* isStaticCall*/ arguments[0] instanceof Class);
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

        private static TypeCompareResult isTypeMoreSpecific(@NotNull Class<?> a, @NotNull Class<?> b) {
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

        private static boolean isMoreSpecific(@NotNull Method a, @NotNull Method b) {
            if (a == b)
                return true;

            switch (isTypeMoreSpecific(a.getReturnType(), b.getReturnType())) {
                case BETTER:
                    return true;
                case WORSE:
                    return false;
            }


            /**
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

        private static Class<?> prepareClassForCompare(Class<?> clazz) {
            return getJavaObjectType(getKotlinClass(clazz));
        }

        @Override
        public void setCallSite()  {
            genMethodClass();
            prepareMetaHandlers();
            changeTargetGuard();
            processSetCallSite();
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
            MethodHandle fallback = DynamicMetaFactory.makeFallBack(mc, caller, type, name, DynamicMetaFactory.INVOKE_TYPE.METHOD);
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

        private boolean isMethodSuitable(@NotNull Method method, boolean skipReceiverCheck) {
            int offset = 0;
            if (skipReceiverCheck) {
                offset = 1;
            }

            if (method.isVarArgs()) {
                /*
                * arguments already includes receiver and we processed empty vararg case
                 */
                if (method.getParameterCount() > arguments.length) {
                    return false;
                }
            } else if (method.getParameterCount() - offset != arguments.length - 1) {
                return false;
            }

            Class<?>[] requiredMethodParameters = method.getParameterTypes();
            for (int i = 0; i < requiredMethodParameters.length; ++i) {
                if (isTypeMoreSpecific(arguments[i + 1 - offset].getClass(), requiredMethodParameters[i]).index >=
                        TypeCompareResult.WORSE.index) {
                    return false;
                }
            }

            return true;
        }

        @NotNull
        private List<Method> filterSuitableMethods(@NotNull List<Method> methods) {
            return filterSuitableMethods(methods, false);
        }

        @NotNull
        private List<Method> filterSuitableMethods(@NotNull List<Method> methods, boolean skipReceiverCheck) {
            return methods.stream().filter(it -> isMethodSuitable(it, skipReceiverCheck)).collect(Collectors.toList());
        }

        @NotNull
        private static Map<Class, Class> builtinClasses = new HashMap<>();
        static {
            builtinClasses.put(java.lang.Byte.class, ByteBuiltins.class);
            builtinClasses.put(java.lang.Double.class, DoubleBuiltins.class);
            builtinClasses.put(java.lang.Float.class, FloatBuiltins.class);
            builtinClasses.put(java.lang.Integer.class, IntBuiltins.class);
            builtinClasses.put(java.lang.Long.class, LongBuiltins.class);
            builtinClasses.put(java.lang.Short.class, ShortBuiltins.class);
            builtinClasses.put(boolean[].class, BooleanArrayBuiltins.class);
            builtinClasses.put(byte[].class, ByteArrayBuiltins.class);
            builtinClasses.put(char[].class, CharArrayBuiltins.class);
            builtinClasses.put(double[].class, DoubleArrayBuiltins.class);
            builtinClasses.put(float[].class, FloatArrayBuiltins.class);
            builtinClasses.put(int[].class, IntArrayBuiltins.class);
            builtinClasses.put(long[].class, LongArrayBuiltins.class);
            builtinClasses.put(short[].class, ShortArrayBuiltins.class);
        }

        @NotNull
        private List<Method> findBuiltins(@NotNull Class methodClass){
            Class builtinClass = builtinClasses.get(methodClass);
            if (builtinClass == null) {
                return Collections.emptyList();
            }
            return fastMethodFilter(Arrays.asList(builtinClass.getDeclaredMethods()));
        }

        @NotNull
        private List<Method> fastMethodFilter(@NotNull List<Method> methods) {
            return methods.stream()
                    .filter(it -> it.getName().equals(name))
                    .distinct()
                    .collect(Collectors.toList());
        }

        private void genMethodClass() {
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

                List<Method> targetMethodList = fastMethodFilter(methods);

                targetMethodList = filterSuitableMethods(targetMethodList);

                if (targetMethodList.isEmpty()){
                    targetMethodList = filterSuitableMethods(findBuiltins(methodClass), true);
                }

                Method targetMethod = findMostSpecific(targetMethodList);

                if (targetMethod == null) {
                    throw new DynamicBindException("Runtime: cannot find target method " + name);
                }

                targetMethod.setAccessible(true);

                try {
                    handle = caller.unreflect(targetMethod);
                } catch (IllegalAccessException e) {
                    throw new DynamicBindException(e.getMessage());
                }
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

        public void setCallSite() {
            genMethodClass();
            processSetCallSite();
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
                    Field field = receiver.getClass().getField(name);
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
