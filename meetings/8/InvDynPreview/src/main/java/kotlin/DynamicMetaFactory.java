package kotlin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.*;
import java.net.BindException;
import java.util.HashMap;
import java.util.Map;


public final class DynamicMetaFactory {
    /*
     * Method handles for guards
     */
    /* package */ static final MethodHandle IS_INSTANCE, IS_NULL, IS_REFERENCES_EQUAL, PERFORM_INVOKE_METHOD;
    /* package */ static final MethodType
            CLASS_INSTANCE_MTYPE = MethodType.methodType(boolean.class, Class.class, Object.class),
            OBJECT_TEST_MTYPE = MethodType.methodType(boolean.class, Object.class),
            TWO_OBJECT_TEST_MTYPE = MethodType.methodType(boolean.class, Object.class, Object.class);
    private static final MethodHandles.Lookup DYNAMIC_LOOKUP = MethodHandles.lookup();
    private static final MethodHandle FIELD_GET, FIELD_SET, INVOKE_METHOD;
    private static final Map<String, String> ASSIGNMENT_OPERATION_COUNTERPARTS = new HashMap<>();
    private static final AssignmentMarker ASSIGNMENT_MARKER = new AssignmentMarker();

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
            FIELD_GET = DYNAMIC_LOOKUP.findStatic(DynamicMetaFactory.class, "fieldGetProxy", mt);
            FIELD_SET = DYNAMIC_LOOKUP.findStatic(DynamicMetaFactory.class, "fieldSetProxy", mt);
            INVOKE_METHOD = DYNAMIC_LOOKUP.findStatic(DynamicMetaFactory.class, "invokeProxy", mtInvoke);
            IS_INSTANCE = DYNAMIC_LOOKUP.findStatic(DynamicGuards.class, "isInstance", CLASS_INSTANCE_MTYPE);
            IS_NULL = DYNAMIC_LOOKUP.findStatic(DynamicGuards.class, "isNull", OBJECT_TEST_MTYPE);
            // [TODO] Object.equals
            IS_REFERENCES_EQUAL = DYNAMIC_LOOKUP.findStatic(DynamicGuards.class, "isReferencesEqual", TWO_OBJECT_TEST_MTYPE);
            PERFORM_INVOKE_METHOD = DYNAMIC_LOOKUP.findVirtual(ObjectInvoker.class, "performInvoke", MethodType.methodType(Object.class, Object[].class));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new DynamicBindException(e);
        }
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("plusAssign", "plus");
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("minusAssign", "minus");
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("timesAssign", "times");
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("divAssign", "div");
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("modAssign", "mod"); // rem assign?!
    }

    public static CallSite bootstrapDynamic(MethodHandles.Lookup caller,
                                            String query,
                                            MethodType type,
                                            String name,
                                            int flags,
                                            String... namedArguments)
            throws IllegalAccessException, NoSuchMethodException, BindException {
        MutableCallSite mc = new MutableCallSite(type);
        INVOKE_TYPE it;
        if (query.equals(INVOKE_TYPE.GET.type)) {
            it = INVOKE_TYPE.GET;
        } else if (query.equals(INVOKE_TYPE.SET.type)) {
            it = INVOKE_TYPE.SET;
        } else if (query.equals(INVOKE_TYPE.METHOD.type)) {
            it = INVOKE_TYPE.METHOD;
        } else {
            throw new UnsupportedOperationException("unknown invoke query");
        }

        MethodHandle mh = makeFallBack(mc, caller, type, name, namedArguments, it);
        mc.setTarget(mh);

        return mc;
    }

    /* package */
    static MethodHandle makeFallBack(@Nullable MutableCallSite mc,
                                     MethodHandles.Lookup caller,
                                     MethodType type,
                                     String name,
                                     String[] namedArguments,
                                     INVOKE_TYPE it) {
        MethodHandle mh = MethodHandles.insertArguments(it.getHandler(), 0, mc, caller, type, name);
        if (it == INVOKE_TYPE.METHOD) {
            mh = MethodHandles.insertArguments(mh, 1, namedArguments, /* allowNamingConversion */true);
        }
        mh = mh.asCollector(Object[].class, type.parameterCount())
                .asType(type);
        return mh;
    }

    private static Object fieldGetProxy(@Nullable MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) throws Throwable {
        //[TODO] Selector
        DynamicSelector selector = DynamicSelector.getFieldSelector(mc, caller, type, name, arguments, INVOKE_TYPE.GET);
        if (!selector.setCallSite()) {
            throw new DynamicBindException("Cannot find getter for field " + name);
        }
        selector.processSetTarget();
        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));
        return call.invokeExact(arguments);
    }

    private static Object fieldSetProxy(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) throws Throwable {
        //[TODO] Selector
        DynamicSelector selector = DynamicSelector.getFieldSelector(mc, caller, type, name, arguments, INVOKE_TYPE.SET);
        if (!selector.setCallSite()) {
            throw new DynamicBindException("Cannot find setter for field " + name);
        }
        selector.processSetTarget();
        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));
        return call.invokeExact(arguments);
    }

    private static Object invokeProxy(MutableCallSite mc,
                                      MethodHandles.Lookup caller,
                                      MethodType type,
                                      String name,
                                      Object[] arguments,
                                      @Nullable String[] namedArguments,
                                      boolean allowNamingConversion
    ) throws Throwable {
        System.out.println("calculation target " + name);
        //[TODO] Selector
        boolean assignmentOperatorConversion = false;
        DynamicSelector selector = DynamicSelector.getMethodSelector(mc, caller, type, name, arguments, namedArguments);
        String operatorCounterpart = ASSIGNMENT_OPERATION_COUNTERPARTS.get(name);
        boolean callSiteMounted = selector.setCallSite();
        if (allowNamingConversion && !callSiteMounted) {
            if (operatorCounterpart != null) {
                assignmentOperatorConversion = true;
                selector.changeName(operatorCounterpart);
                callSiteMounted = selector.setCallSite();
            }

            //it can be field/property with lambda
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

        Object result = call.invokeExact(arguments);

        if ((operatorCounterpart != null) && (!assignmentOperatorConversion)) {
            return ASSIGNMENT_MARKER;
        }

        if (selector.isReturnUnit()) {
            return Unit.INSTANCE;
        }

        return result;
    }

    /* package */ enum INVOKE_TYPE {
        GET("getField", "get", FIELD_GET),
        SET("setField", "set", FIELD_SET),
        METHOD("invoke", "", INVOKE_METHOD);

        private final String type;
        private final String javaPrefix;
        private final MethodHandle mh;

        private INVOKE_TYPE(String type, String javaPrefix, MethodHandle mh) {
            this.type = type;
            this.javaPrefix = javaPrefix;
            this.mh = mh;
        }

        public final MethodHandle getHandler() {
            return mh;
        }

        public final String getJavaPrefix() {
            return javaPrefix;
        }
    }

    /* package */ static class ObjectInvoker {
        boolean isReturnUnit;
        @NotNull
        private MethodHandle getterCall;
        @NotNull
        private MethodHandles.Lookup caller;
        @Nullable
        private String[] namedArguments;
        @Nullable
        private MethodHandle cachedCall;
        @NotNull
        private Class[] cachedArguments;

        /* package */ ObjectInvoker(@NotNull MethodHandle getterCall, @NotNull MethodHandles.Lookup caller, @Nullable String[] namedArguments) {
            this(getterCall, caller, namedArguments, null, new Class[]{});
        }

        /* package */ ObjectInvoker(@NotNull MethodHandle getterCall, @NotNull MethodHandles.Lookup caller, @Nullable String[] namedArguments, @Nullable MethodHandle cachedCall, @NotNull Class[] cachedArguments) {
            this.getterCall = getterCall;
            this.caller = caller;
            this.namedArguments = namedArguments;
            this.cachedCall = cachedCall;
            this.cachedArguments = cachedArguments;
            this.isReturnUnit = getterCall.type().returnType().equals(void.class);
        }

        private boolean checkCache(Object[] arguments) {
            if (cachedCall == null) return false;
            if (cachedArguments.length != arguments.length) return false;
            for (int i = 0; i < cachedArguments.length; ++i) {
                if (!cachedArguments[i].equals(arguments[i].getClass()))
                    return false;
            }
            return true;
        }

        /* package */ Object performInvoke(Object[] arguments) throws Throwable {
            System.out.println("lambda calculated");
            assert arguments.length > 0;

            Object field = getterCall.invoke(arguments[0]);
            arguments[0] = field;

            if (!checkCache(arguments)) {
                System.out.println("rewrite lambda cache");
                cachedCall = DynamicOverloadResolution.resolveMethod(caller, "invoke", arguments, namedArguments, /* isStaticCall */false);
                if (cachedCall == null) {
                    throw new DynamicBindException("Cannot invoke target object");
                }
                if (cachedArguments.length != arguments.length) {
                    cachedArguments = new Class[arguments.length];
                }
                for (int i = 0; i < arguments.length; ++i) {
                    cachedArguments[i] = arguments[i].getClass();
                }
                cachedCall = cachedCall
                        .asSpreader(Object[].class, arguments.length)
                        .asType(MethodType.methodType(Object.class, Object[].class));
            }

            assert cachedCall != null;
            Object result = cachedCall.invokeExact(arguments);

            if (isReturnUnit) {
                return Unit.INSTANCE;
            }
            return result;
        }
    }

    public final static class AssignmentMarker {
        private AssignmentMarker() {
        }
    }
}
