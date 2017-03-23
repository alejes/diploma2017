package kotlin;

import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;

import static kotlin.DynamicMetaFactory.*;

/* package */ abstract class DynamicSelector {
    protected final Object[] arguments;
    protected final MethodHandles.Lookup caller;
    @Nullable
    protected final MutableCallSite mc;
    protected final MethodType type;
    protected final boolean isStaticCall;
    protected String name;
    protected MethodHandle handle;
    protected boolean isReturnUnit = false;

    private DynamicSelector(Object[] arguments, @Nullable MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, boolean isStaticCall) {
        this.arguments = arguments;
        this.mc = mc;
        this.caller = caller;
        this.type = type;
        this.name = name;
        this.isStaticCall = isStaticCall;
    }

    /* package */
    static DynamicSelector getFieldSelector(@Nullable MutableCallSite mc,
                                            MethodHandles.Lookup caller,
                                            MethodType type,
                                            String name,
                                            Object[] arguments,
                                            INVOKE_TYPE it) {
        return new FieldSelector(mc, caller, type, name, arguments, it);
    }

    /* package */
    static DynamicSelector getMethodSelector(MutableCallSite mc,
                                             MethodHandles.Lookup caller,
                                             MethodType type,
                                             String name,
                                             Object[] arguments,
                                             @Nullable String[] namedArguments) {
        return new MethodSelector(mc, caller, type, name, arguments, namedArguments);
    }

    /* package */
    static DynamicSelector getInvokerSelector(MutableCallSite mc,
                                              MethodHandles.Lookup caller,
                                              MethodType type,
                                              String name,
                                              Object[] arguments,
                                              @Nullable String[] namedArguments) {
        return new InvokerSelector(mc, caller, type, name, arguments, namedArguments);
    }


    /* package */ boolean isReturnUnit() {
        return isReturnUnit;
    }

    protected void prepareMetaHandlers() {
        //cached in groovy
        if (isStaticCall) {
            MethodType staticType = type.dropParameterTypes(0, 1);
            handle = MethodHandles.explicitCastArguments(handle, staticType);
            handle = MethodHandles.dropArguments(handle, 0, Class.class);
        } else {
            handle = MethodHandles.explicitCastArguments(handle, type);
        }
    }

    protected void changeTargetGuard() {
        MethodHandle fallback = makeFallBack(mc, caller, type, name, null, INVOKE_TYPE.METHOD);
        Class<?>[] handleParameters = handle.type().parameterArray();
        for (int i = 0; i < arguments.length; ++i) {
            MethodHandle guard;
            if (arguments[i] == null) {
                guard = IS_NULL
                        .asType(MethodType.methodType(boolean.class, handleParameters[i]));
            } else {
                guard = IS_INSTANCE
                        .bindTo(arguments[i].getClass())
                        .asType(MethodType.methodType(boolean.class, handleParameters[i]));
            }
            Class[] dropTypes = new Class[i];
            System.arraycopy(handleParameters, 0, dropTypes, 0, dropTypes.length);
            guard = MethodHandles.dropArguments(guard, 0, dropTypes);
            handle = MethodHandles.guardWithTest(guard, handle, fallback);
        }
    }


    /* package */
    abstract boolean setCallSite() throws DynamicBindException;

    /* package */ void changeName(String name) {
        this.name = name;
    }

    /* package */ MethodHandle getMethodHandle() {
        return handle;
    }

    /* package */ void processSetTarget() {
        if (mc != null) {
            mc.setTarget(handle);
        }
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

    private final static class InvokerSelector extends DynamicSelector {
        @Nullable
        private String[] namedArguments;

        private InvokerSelector(@Nullable MutableCallSite mc,
                                MethodHandles.Lookup caller,
                                MethodType type,
                                String name,
                                Object[] arguments,
                                @Nullable String[] namedArguments) {
            super(arguments, mc, caller, type, name, /* isStaticCall */ arguments[0] instanceof Class);
            this.namedArguments = namedArguments;
        }

        @Override
        /* package */ boolean setCallSite() {
            if (!genMethodClass()) {
                return false;
            }
            prepareMetaHandlers();
            if (mc != null) {
                changeTargetGuard();
                processSetTarget();
            }
            return true;
        }

        private boolean genMethodClass() {
            MethodHandle getterHandle = DynamicOverloadResolution.resolveFieldOrPropertyGetter(caller, name, new Object[]{arguments[0]}, /* isStaticCall */false);
            if (getterHandle == null) {
                return false;
            }

            ObjectInvoker objectInvoker = new ObjectInvoker(getterHandle, caller, namedArguments);

            handle = PERFORM_INVOKE_METHOD.bindTo(objectInvoker).
                    asCollector(Object[].class, type.parameterCount())
                    .asType(type);
            ;

            return true;
        }
    }


    private final static class MethodSelector extends DynamicSelector {
        @Nullable
        private String[] namedArguments;

        private MethodSelector(@Nullable MutableCallSite mc,
                               MethodHandles.Lookup caller,
                               MethodType type,
                               String name,
                               Object[] arguments,
                               @Nullable String[] namedArguments) {
            super(arguments, mc, caller, type, name, /* isStaticCall*/ arguments[0] instanceof Class);
            this.namedArguments = namedArguments;
        }

        @Override
        /* package */ boolean setCallSite() {
            if (!genMethodClass()) {
                return false;
            }
            prepareMetaHandlers();
            if (mc != null) {
                changeTargetGuard();
                processSetTarget();
            }
            return true;
        }


        private boolean genMethodClass() {
            handle = DynamicOverloadResolution.resolveMethod(caller, name, arguments, namedArguments, isStaticCall);
            if (handle == null) {
                return false;
            }
            isReturnUnit = handle.type().returnType().equals(void.class);
            return true;
        }
    }

    private final static class FieldSelector extends DynamicSelector {
        private final INVOKE_TYPE it;

        private FieldSelector(@Nullable MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments, INVOKE_TYPE it) {
            // [TODO] static call for fields
            super(arguments, mc, caller, type, name, /* isStaticCall */ false);
            this.it = it;
        }

        @Override
        /* package */  boolean setCallSite() {
            if (!genMethodClass()) {
                return false;
            }
            prepareMetaHandlers();
            changeTargetGuard();
            processSetTarget();
            return true;
        }

        private boolean genMethodClass() {
            switch (it) {
                case GET:
                    handle = DynamicOverloadResolution.resolveFieldOrPropertyGetter(caller, name, arguments, isStaticCall);
                    break;
                case SET:
                    handle = DynamicOverloadResolution.resolveFieldOrPropertySetter(caller, name, arguments, isStaticCall);
                    break;
                default:
                    throw new DynamicBindException("Wrong invoke type for field");
            }
            if (handle == null) {
                return false;
            }
            isReturnUnit = handle.type().returnType().equals(void.class);
            return true;
        }
    }

}
