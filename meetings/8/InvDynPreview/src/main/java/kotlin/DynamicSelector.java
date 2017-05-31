package kotlin;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static kotlin.DynamicMetafactory.*;

/* package */ abstract class DynamicSelector {
    private static final int COMPOUND_ASSIGNMENT_FLAG = 1;
    protected final Object[] arguments;
    protected final MethodHandles.Lookup caller;
    protected final DynamicCallSite mc;
    protected final MethodType type;
    protected final boolean isStaticCall;
    protected final InvokeType invokeType;
    /* Nullable */
    protected final String[] namedArguments;
    protected final int flags;
    protected String name;
    protected MethodHandle handle;
    protected boolean isReturnUnit = false;
    protected boolean addGuardsForArguments = true;

    private DynamicSelector(Object[] arguments,
                            DynamicCallSite mc,
                            MethodHandles.Lookup caller,
                            MethodType type,
                            String name,
                            int flags,
                            InvokeType invokeType,
                            /* Nullable */  String[] namedArguments,
                            boolean isStaticCall) {
        this.arguments = arguments;
        this.mc = mc;
        this.caller = caller;
        this.type = type;
        this.name = name;
        this.invokeType = invokeType;
        this.flags = flags;
        this.namedArguments = namedArguments;
        this.isStaticCall = isStaticCall;
    }

    /* package */
    static DynamicSelector getFieldSelector(DynamicCallSite mc,
                                            MethodHandles.Lookup caller,
                                            MethodType type,
                                            String name,
                                            Object[] arguments,
                                            InvokeType it) {
        return new FieldSelector(mc, caller, type, name, arguments, it);
    }

    /* package */
    static DynamicSelector getMethodSelector(DynamicCallSite mc,
                                             MethodHandles.Lookup caller,
                                             MethodType type,
                                             String name,
                                             Object[] arguments,
                                             int flags,
                                             /* Nullable */  String[] namedArguments) {
        return new MethodSelector(mc, caller, type, name, arguments, flags, namedArguments);
    }

    /* package */
    static DynamicSelector getInvokerSelector(DynamicCallSite mc,
                                              MethodHandles.Lookup caller,
                                              MethodType type,
                                              String name,
                                              Object[] arguments,
                                              /* Nullable */ String[] namedArguments) {
        return new InvokerSelector(mc, caller, type, name, arguments, namedArguments);
    }

    protected void prepareMetaHandlers() {
        if (handle.isVarargsCollector()) {
            int parametersCount = handle.type().parameterCount();
            Class<?> varargType = handle.type().parameterType(parametersCount - 1);
            handle = handle.asCollector(varargType, type.parameterCount() - parametersCount + 1)
                    .asType(type);
        }
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
        MethodHandle fallback = makeFallBack(mc, caller, type, name, flags, namedArguments, invokeType);
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
            if (!addGuardsForArguments) {
                break;
            }
        }
    }

    protected void filterResult(boolean compoundAssignment) {
        if (compoundAssignment && ((flags & COMPOUND_ASSIGNMENT_FLAG) == COMPOUND_ASSIGNMENT_FLAG)) {
            handle = MethodHandles.filterReturnValue(handle, FILTER_COMPOUND_ASSIGNMENT);
        } else if (isReturnUnit && !type.returnType().equals(void.class)) {
            handle = MethodHandles.filterReturnValue(handle, FILTER_UNIT);
        }
    }

    protected abstract boolean genMethodClass();

    /* package */  boolean setCallSite() {
        return setCallSite(false);
    }

    /* package */  boolean setCallSite(boolean compoundAssignment) {
        if (!genMethodClass()) {
            return false;
        }
        prepareMetaHandlers();
        changeTargetGuard();
        filterResult(compoundAssignment);
        processSetTarget();
        return true;
    }

    /* package */ void changeName(String name) {
        this.name = name;
    }

    /* package */ MethodHandle getMethodHandle() {
        return handle;
    }

    private void processSetTarget() {
        mc.setTarget(handle);
    }

    /* package */ enum TypeCompareResult {
        BETTER(0),
        EQUAL(1),
        BOXING(2),
        WORSE(3);
        int index;

        /* package */ TypeCompareResult(int index) {
            this.index = index;
        }

    }

    private final static class InvokerSelector extends DynamicSelector {
        private InvokerSelector(DynamicCallSite mc,
                                MethodHandles.Lookup caller,
                                MethodType type,
                                String name,
                                Object[] arguments,
                                /* Nullable */  String[] namedArguments) {
            super(arguments,
                    mc,
                    caller,
                    type,
                    name,
                    /* flags */ 0,
                    InvokeType.METHOD,
                    namedArguments,
                    /* isStaticCall */ arguments[0] instanceof Class);
        }

        @Override
        protected boolean genMethodClass() {
            MethodHandle getterHandle = DynamicOverloadResolution.resolveFieldOrPropertyGetter(caller,
                    name,
                    new Object[]{arguments[0]},
                    /* isStaticCall */false);
            if (getterHandle == null) {
                return false;
            }

            ObjectInvoker objectInvoker = new ObjectInvoker(getterHandle, caller, namedArguments);
            // guards will be checked in ObjectInvoker
            addGuardsForArguments = false;

            handle = PERFORM_INVOKE_METHOD.bindTo(objectInvoker).
                    asCollector(Object[].class, type.parameterCount())
                    .asType(type);

            return true;
        }
    }

    private final static class MethodSelector extends DynamicSelector {

        private MethodSelector(DynamicCallSite mc,
                               MethodHandles.Lookup caller,
                               MethodType type,
                               String name,
                               Object[] arguments,
                               int flags,
                               /* Nullable */  String[] namedArguments) {
            super(arguments,
                    mc,
                    caller,
                    type,
                    name,
                    flags,
                    InvokeType.METHOD,
                    namedArguments,
                    /* isStaticCall */ arguments[0] instanceof Class);
        }

        @Override
        protected boolean genMethodClass() {
            handle = DynamicOverloadResolution.resolveMethod(caller, name, arguments, namedArguments, isStaticCall);
            if (handle == null) {
                return false;
            }
            isReturnUnit = handle.type().returnType().equals(void.class);
            return true;
        }
    }

    private final static class FieldSelector extends DynamicSelector {
        private FieldSelector(DynamicCallSite mc,
                              MethodHandles.Lookup caller,
                              MethodType type,
                              String name,
                              Object[] arguments,
                              InvokeType it) {
            super(arguments,
                    mc,
                    caller,
                    type,
                    name,
                    /* flags */ 0,
                    it,
                    /* namedArguments */ null,
                    /* isStaticCall */ false);
        }

        @Override
        protected boolean genMethodClass() {
            switch (invokeType) {
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
