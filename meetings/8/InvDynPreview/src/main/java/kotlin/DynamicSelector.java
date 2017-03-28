package kotlin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;

import static kotlin.DynamicMetafactory.*;

/* package */ abstract class DynamicSelector {
    @NotNull
    protected final Object[] arguments;
    @NotNull
    protected final MethodHandles.Lookup caller;
    @NotNull
    protected final MutableCallSite mc;
    @NotNull
    protected final MethodType type;
    protected final boolean isStaticCall;
    @NotNull
    protected final InvokeType invokeType;
    @NotNull
    protected String name;
    protected MethodHandle handle;
    protected boolean isReturnUnit = false;
    protected boolean addGuardsForArguments = true;

    private DynamicSelector(@NotNull Object[] arguments,
                            @NotNull MutableCallSite mc,
                            @NotNull MethodHandles.Lookup caller,
                            @NotNull MethodType type,
                            @NotNull String name,
                            @NotNull InvokeType invokeType,
                            boolean isStaticCall) {
        this.arguments = arguments;
        this.mc = mc;
        this.caller = caller;
        this.type = type;
        this.name = name;
        this.invokeType = invokeType;
        this.isStaticCall = isStaticCall;
    }

    /* package */
    static DynamicSelector getFieldSelector(@NotNull MutableCallSite mc,
                                            @NotNull MethodHandles.Lookup caller,
                                            @NotNull MethodType type,
                                            @NotNull String name,
                                            @NotNull Object[] arguments,
                                            @NotNull InvokeType it) {
        return new FieldSelector(mc, caller, type, name, arguments, it);
    }

    /* package */
    static DynamicSelector getMethodSelector(@NotNull MutableCallSite mc,
                                             @NotNull MethodHandles.Lookup caller,
                                             @NotNull MethodType type,
                                             @NotNull String name,
                                             @NotNull Object[] arguments,
                                             @Nullable String[] namedArguments) {
        return new MethodSelector(mc, caller, type, name, arguments, namedArguments);
    }

    /* package */
    static DynamicSelector getInvokerSelector(@NotNull MutableCallSite mc,
                                              @NotNull MethodHandles.Lookup caller,
                                              @NotNull MethodType type,
                                              @NotNull String name,
                                              @NotNull Object[] arguments,
                                              @Nullable String[] namedArguments) {
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
        MethodHandle fallback = makeFallBack(mc, caller, type, name, null, invokeType);
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

    protected abstract boolean genMethodClass();

    /* package */  boolean setCallSite() {
        if (!genMethodClass()) {
            return false;
        }
        prepareMetaHandlers();
        changeTargetGuard();
        processSetTarget();
        return true;
    }

    /* package */ boolean isReturnUnit() {
        return isReturnUnit;
    }

    /* package */ void changeName(@NotNull String name) {
        this.name = name;
    }

    /* package */ MethodHandle getMethodHandle() {
        return handle;
    }

    /* package */ void processSetTarget() {
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
        @Nullable
        private String[] namedArguments;

        private InvokerSelector(@NotNull MutableCallSite mc,
                                @NotNull MethodHandles.Lookup caller,
                                @NotNull MethodType type,
                                @NotNull String name,
                                @NotNull Object[] arguments,
                                @Nullable String[] namedArguments) {
            super(arguments, mc, caller, type, name, InvokeType.METHOD, /* isStaticCall */ arguments[0] instanceof Class);
            this.namedArguments = namedArguments;
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
        @Nullable
        private String[] namedArguments;

        private MethodSelector(@NotNull MutableCallSite mc,
                               @NotNull MethodHandles.Lookup caller,
                               @NotNull MethodType type,
                               @NotNull String name,
                               @NotNull Object[] arguments,
                               @Nullable String[] namedArguments) {
            super(arguments, mc, caller, type, name, InvokeType.METHOD, /* isStaticCall */ arguments[0] instanceof Class);
            this.namedArguments = namedArguments;
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
        private FieldSelector(@NotNull MutableCallSite mc,
                              @NotNull MethodHandles.Lookup caller,
                              @NotNull MethodType type,
                              @NotNull String name,
                              @NotNull Object[] arguments,
                              @NotNull InvokeType it) {
            // [TODO] static call for fields
            super(arguments, mc, caller, type, name, it, /* isStaticCall */ false);
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
