package kotlin;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.BindException;
import java.util.*;
import java.util.stream.Collectors;

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

    public abstract void setCallSite() throws BindException;

    public MethodHandle getMethodHandle() {
        return handle;
    }

    private enum TYPE_COMPARE_RESULT {
        BETTER(0),
        EQUAL(1),
        BOXING(2),
        WORSE(3);
        int index;

        TYPE_COMPARE_RESULT(int index) {
            this.index = index;
        }

    }

    private static class MethodSelector extends DynamicSelector {
        private MethodSelector(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) {
            super(arguments, mc, caller, type, name, /* isStaticCall*/ arguments[0] instanceof Class);
        }

        private static Method findMostSpecific(@NotNull List<Method> methods) throws BindException {
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

        private static TYPE_COMPARE_RESULT isTypeMoreSpecific(@NotNull Class<?> a, @NotNull Class<?> b) {
            if (a == b) {
                return TYPE_COMPARE_RESULT.EQUAL;
            }
            Class<?> first = prepareClassForCompare(a);
            Class<?> second = prepareClassForCompare(b);
            if (first == second) {
                return TYPE_COMPARE_RESULT.BOXING;
            }

            if (second.isAssignableFrom(first)) {
                return TYPE_COMPARE_RESULT.BETTER;
            } else {
                return TYPE_COMPARE_RESULT.WORSE;
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

            int minimumCompareResult = TYPE_COMPARE_RESULT.WORSE.index;
            for (int i = 0; i < aParameters.length; ++i) {
                TYPE_COMPARE_RESULT compareResult = isTypeMoreSpecific(aParameters[i], bParameters[i]);
                if (compareResult == TYPE_COMPARE_RESULT.WORSE) {
                    return false;
                }
                minimumCompareResult = Math.min(minimumCompareResult, compareResult.index);
            }

            return minimumCompareResult <= TYPE_COMPARE_RESULT.BETTER.index;
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
        public void setCallSite() throws BindException {
            genMethodClass();
            processSetCallSite();
        }

        private void processSetCallSite() {
            //cached in groovy
            if (isStaticCall) {
                MethodType staticType = type.dropParameterTypes(0, 1);
                handle = MethodHandles.explicitCastArguments(handle, staticType);
                handle = MethodHandles.dropArguments(handle, 0, Class.class);
            } else {
                handle = MethodHandles.explicitCastArguments(handle, type);
            }

            mc.setTarget(handle);
        }

        private boolean isMethodSuitable(@NotNull Method method) {
            if (method.isVarArgs()) {
                /*
                * arguments already includes receiver and we processed empty vararg case
                 */
                if (method.getParameterCount() > arguments.length) {
                    return false;
                }
            } else if (method.getParameterCount() != arguments.length - 1) {
                return false;
            }

            Class<?>[] requiredMethodParameters = method.getParameterTypes();
            for (int i = 0; i < requiredMethodParameters.length; ++i) {
                if (isTypeMoreSpecific(arguments[i + 1].getClass(), requiredMethodParameters[i]).index >=
                        TYPE_COMPARE_RESULT.WORSE.index) {
                    return false;
                }
            }

            return true;
        }

        @NotNull
        private List<Method> filterSuitableMethods(@NotNull List<Method> methods) {
            return methods.stream().filter(this::isMethodSuitable).collect(Collectors.toList());
        }

        private void genMethodClass() throws BindException {
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

                List<Method> targetMethodList = methods.stream()
                        .distinct()
                        .filter(it -> it.getName().equals(name))
                        .collect(Collectors.toList());

                targetMethodList = filterSuitableMethods(targetMethodList);

                Method targetMethod = findMostSpecific(targetMethodList);

                if (targetMethod == null) {
                    throw new BindException("Runtime: cannot find target method " + name);
                }

                targetMethod.setAccessible(true);

                try {
                    handle = caller.unreflect(targetMethod);
                } catch (IllegalAccessException e) {
                    throw new BindException(e.getMessage());
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

        public void setCallSite() throws BindException {
            genMethodClass();
            processSetCallSite();
        }


        private void processSetCallSite() {
            //cached in groovy
            handle = MethodHandles.explicitCastArguments(handle, type);
            mc.setTarget(handle);
        }

        private void genMethodClass() throws BindException {
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
                    throw new BindException(e.getMessage());
                }
            }
        }
    }

}
