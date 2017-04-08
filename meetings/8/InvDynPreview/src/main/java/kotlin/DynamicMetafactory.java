package kotlin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.*;
import java.net.BindException;
import java.util.HashMap;
import java.util.Map;


public final class DynamicMetafactory {
    /*
     * Method handles for guards
     */
    /* package */ static final MethodHandle IS_INSTANCE, IS_NULL, PERFORM_INVOKE_METHOD, FILTER_UNIT, FILTER_COMPOUND_ASSIGNMENT;
    /* package */ static final CompoundAssignmentPerformMarker COMPOUND_ASSIGNMENT_PERFORM_MARKER = new CompoundAssignmentPerformMarker();
    private static final MethodType
            CLASS_INSTANCE_MTYPE = MethodType.methodType(boolean.class, Class.class, Object.class),
            OBJECT_TEST_MTYPE = MethodType.methodType(boolean.class, Object.class),
            CONSTANT_RETURN_MTYPE = MethodType.methodType(Object.class);
    private static final MethodHandles.Lookup DYNAMIC_LOOKUP = MethodHandles.lookup();
    private static final MethodHandle FIELD_GET, FIELD_SET, INVOKE_METHOD;
    private static final Map<String, String> ASSIGNMENT_OPERATION_COUNTERPARTS = new HashMap<>();

    static {
        MethodType mt = MethodType.methodType(Object.class,
                MutableCallSite.class,
                MethodHandles.Lookup.class,
                MethodType.class,
                String.class,
                Object[].class);
        MethodType mtInvoke = MethodType.methodType(Object.class,
                MutableCallSite.class,
                MethodHandles.Lookup.class,
                MethodType.class,
                String.class,
                Object[].class,
                String[].class,
                boolean.class);
        try {
            FIELD_GET = DYNAMIC_LOOKUP.findStatic(DynamicMetafactory.class, "fieldGetProxy", mt);
            FIELD_SET = DYNAMIC_LOOKUP.findStatic(DynamicMetafactory.class, "fieldSetProxy", mt);
            INVOKE_METHOD = DYNAMIC_LOOKUP.findStatic(DynamicMetafactory.class, "invokeProxy", mtInvoke);
            IS_INSTANCE = DYNAMIC_LOOKUP.findStatic(DynamicGuards.class, "isInstance", CLASS_INSTANCE_MTYPE);
            IS_NULL = DYNAMIC_LOOKUP.findStatic(DynamicGuards.class, "isNull", OBJECT_TEST_MTYPE);
            FILTER_UNIT = DYNAMIC_LOOKUP.findStatic(DynamicFilters.class, "returnUnit", CONSTANT_RETURN_MTYPE);
            FILTER_COMPOUND_ASSIGNMENT = DYNAMIC_LOOKUP.findStatic(DynamicFilters.class, "returnCompoundAssignmentPerformMarker", CONSTANT_RETURN_MTYPE);
            PERFORM_INVOKE_METHOD = DYNAMIC_LOOKUP.findVirtual(ObjectInvoker.class,
                    "performInvoke",
                    MethodType.methodType(Object.class, Object[].class));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new DynamicBindException(e);
        }
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("plusAssign", "plus");
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("minusAssign", "minus");
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("timesAssign", "times");
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("divAssign", "div");
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("modAssign", "mod"); // rem assign?!
    }

    @SuppressWarnings("unused")
    public static CallSite bootstrapDynamic(MethodHandles.Lookup caller,
                                            String query,
                                            MethodType type,
                                            String name,
                                            int flags,
                                            String... namedArguments)
            throws IllegalAccessException, NoSuchMethodException, BindException {
        MutableCallSite mc = new MutableCallSite(type);
        InvokeType it;
        if (query.equals(InvokeType.GET.type)) {
            it = InvokeType.GET;
        } else if (query.equals(InvokeType.SET.type)) {
            it = InvokeType.SET;
        } else if (query.equals(InvokeType.METHOD.type)) {
            it = InvokeType.METHOD;
        } else {
            throw new UnsupportedOperationException("Unknown invoke query");
        }

        MethodHandle mh = makeFallBack(mc, caller, type, name, namedArguments, it);
        mc.setTarget(mh);

        return mc;
    }

    /* package */
    @NotNull
    static MethodHandle makeFallBack(@NotNull MutableCallSite mc,
                                     @NotNull MethodHandles.Lookup caller,
                                     @NotNull MethodType type,
                                     @NotNull String name,
                                     @Nullable String[] namedArguments,
                                     @NotNull InvokeType it) {
        MethodHandle mh = MethodHandles.insertArguments(it.getHandler(), 0, mc, caller, type, name);
        if (it == InvokeType.METHOD) {
            mh = MethodHandles.insertArguments(mh, 1, namedArguments, /* allowNamingConversion */true);
        }
        mh = mh.asCollector(Object[].class, type.parameterCount())
                .asType(type);
        return mh;
    }

    @SuppressWarnings("unused")
    private static Object fieldGetProxy(@NotNull MutableCallSite mc,
                                        @NotNull MethodHandles.Lookup caller,
                                        @NotNull MethodType type,
                                        @NotNull String name,
                                        @NotNull Object[] arguments) throws Throwable {
        //[TODO] Selector
        DynamicSelector selector = DynamicSelector.getFieldSelector(mc, caller, type, name, arguments, InvokeType.GET);
        if (!selector.setCallSite()) {
            throw new DynamicBindException("Cannot find getter for field " + name);
        }
        selector.processSetTarget();
        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));
        return call.invokeExact(arguments);
    }

    @SuppressWarnings("unused")
    private static Object fieldSetProxy(@NotNull MutableCallSite mc,
                                        @NotNull MethodHandles.Lookup caller,
                                        @NotNull MethodType type,
                                        @NotNull String name,
                                        @NotNull Object[] arguments) throws Throwable {
        //[TODO] Selector
        DynamicSelector selector = DynamicSelector.getFieldSelector(mc, caller, type, name, arguments, InvokeType.SET);
        if (!selector.setCallSite()) {
            throw new DynamicBindException("Cannot find setter for field " + name);
        }
        selector.processSetTarget();
        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));
        return call.invokeExact(arguments);
    }

    @SuppressWarnings("unused")
    private static Object invokeProxy(@NotNull MutableCallSite mc,
                                      @NotNull MethodHandles.Lookup caller,
                                      @NotNull MethodType type,
                                      @NotNull String name,
                                      @NotNull Object[] arguments,
                                      @Nullable String[] namedArguments,
                                      boolean allowNamingConversion
    ) throws Throwable {
        //[TODO] Selector
        DynamicSelector selector = DynamicSelector.getMethodSelector(mc, caller, type, name, arguments, namedArguments);
        String operatorCounterpart = ASSIGNMENT_OPERATION_COUNTERPARTS.get(name);
        boolean callSiteMounted = selector.setCallSite();
        if (allowNamingConversion && !callSiteMounted) {
            if (operatorCounterpart != null) {
                selector.changeName(operatorCounterpart);
                callSiteMounted = selector.setCallSite(true);
            }

            //invokeType can be field/property with lambda
            if (!callSiteMounted) {
                selector = DynamicSelector.getInvokerSelector(mc, caller, type, name, arguments, namedArguments);
                callSiteMounted = selector.setCallSite();
            }
        }

        if (!callSiteMounted) {
            throw new DynamicBindException("Cannot find target method " + name);
        }

        selector.processSetTarget();

        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));

        return call.invokeExact(arguments);
    }

    /**
     * @throws DynamicBindException - always
     */
    @SuppressWarnings("unused")
    public static Throwable processNotFoundSelector() {
        throw new DynamicBindException("Cannot find selector");
    }

    /* package */ enum InvokeType {
        GET("getField", "get", FIELD_GET),
        SET("setField", "set", FIELD_SET),
        METHOD("invoke", "", INVOKE_METHOD);

        @NotNull
        private final String type;
        @NotNull
        private final String javaPrefix;
        @NotNull
        private final MethodHandle mh;

        private InvokeType(@NotNull String type,
                           @NotNull String javaPrefix,
                           @NotNull MethodHandle mh) {
            this.type = type;
            this.javaPrefix = javaPrefix;
            this.mh = mh;
        }

        @NotNull
        public final MethodHandle getHandler() {
            return mh;
        }

        @NotNull
        public final String getJavaPrefix() {
            return javaPrefix;
        }
    }

    /* package */ static class ObjectInvoker {
        @NotNull
        private final MethodHandle getterCall;
        @NotNull
        private final MethodHandles.Lookup caller;
        @Nullable
        private final String[] namedArguments;
        private final boolean isReturnUnit;
        @NotNull
        private ThreadLocal<MethodHandle> cachedCall;
        @NotNull
        private ThreadLocal<Class[]> cachedArguments;

        /* package */ ObjectInvoker(@NotNull MethodHandle getterCall,
                                    @NotNull MethodHandles.Lookup caller,
                                    @Nullable String[] namedArguments) {
            this(getterCall, caller, namedArguments, null, new Class[]{});
        }

        /* package */ ObjectInvoker(@NotNull MethodHandle getterCall,
                                    @NotNull MethodHandles.Lookup caller,
                                    @Nullable String[] namedArguments,
                                    @Nullable MethodHandle cachedCall,
                                    @NotNull Class[] cachedArguments) {
            this.getterCall = getterCall;
            this.caller = caller;
            this.namedArguments = namedArguments;
            this.cachedCall = ThreadLocal.withInitial(() -> cachedCall);
            this.cachedArguments = ThreadLocal.withInitial(() -> cachedArguments);
            this.isReturnUnit = getterCall.type().returnType().equals(void.class);
        }

        private boolean checkCache(@Nullable MethodHandle cachedCall, @NotNull Class[] cachedArguments, Object[] arguments) {
            if (cachedCall == null) return false;
            if (cachedArguments.length != arguments.length) return false;
            for (int i = 0; i < cachedArguments.length; ++i) {
                if (!cachedArguments[i].equals(arguments[i].getClass()))
                    return false;
            }
            return true;
        }

        @SuppressWarnings("unused")
        /* package */ Object performInvoke(Object[] arguments) throws Throwable {
            assert arguments.length > 0;

            Object field = getterCall.invoke(arguments[0]);
            arguments[0] = field;

            MethodHandle targetCall = cachedCall.get();
            Class[] currentCachedArguments = cachedArguments.get();
            if (!checkCache(targetCall, currentCachedArguments, arguments)) {
                targetCall = DynamicOverloadResolution.resolveMethod(caller,
                        "invoke",
                        arguments,
                        namedArguments,
                        /* isStaticCall */false);

                if (targetCall == null) {
                    throw new DynamicBindException("Cannot invoke target object");
                }
                if (currentCachedArguments.length != arguments.length) {
                    currentCachedArguments = new Class[arguments.length];
                }
                for (int i = 0; i < arguments.length; ++i) {
                    currentCachedArguments[i] = arguments[i].getClass();
                }
                targetCall = targetCall
                        .asSpreader(Object[].class, arguments.length)
                        .asType(MethodType.methodType(Object.class, Object[].class));
                cachedCall.set(targetCall);
                cachedArguments.set(currentCachedArguments);
            }

            assert targetCall != null;
            Object result = targetCall.invokeExact(arguments);

            if (isReturnUnit) {
                return Unit.INSTANCE;
            }
            return result;
        }
    }

    public final static class CompoundAssignmentPerformMarker {
        private CompoundAssignmentPerformMarker() {
        }
    }
}
