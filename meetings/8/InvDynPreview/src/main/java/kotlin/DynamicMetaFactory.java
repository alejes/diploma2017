package kotlin;

import java.lang.invoke.*;
import java.net.BindException;


public class DynamicMetaFactory {
    private static final MethodHandles.Lookup DYNAMIC_LOOKUP = MethodHandles.lookup();
    private static final MethodHandle FIELD_GET, FIELD_SET, INVOKE_METHOD;
    /*
     * Method handles for guards
     */
    public static final MethodHandle IS_INSTANCE;

    public static final MethodType
        CLASS_INSTANCE_MTYPE = MethodType.methodType(boolean.class, Class.class, Object.class);

    static {
        MethodType mt = MethodType.methodType(Object.class, MutableCallSite.class, MethodHandles.Lookup.class, MethodType.class, String.class, Object[].class);
        try {
            FIELD_GET = DYNAMIC_LOOKUP.findStatic(DynamicMetaFactory.class, "fieldGetProxy", mt);
            FIELD_SET = DYNAMIC_LOOKUP.findStatic(DynamicMetaFactory.class, "fieldSetProxy", mt);
            INVOKE_METHOD = DYNAMIC_LOOKUP.findStatic(DynamicMetaFactory.class, "invokeProxy", mt);
            IS_INSTANCE = DYNAMIC_LOOKUP.findStatic(DynamicGuards.class, "isInstance", CLASS_INSTANCE_MTYPE);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            //[TODO] chose exception
            throw new RuntimeException(e.getMessage());
        }
    }

    public static CallSite bootstrapDynamic(MethodHandles.Lookup caller,
                                            String query,
                                            MethodType type,
                                            String name, int flags)
            throws IllegalAccessException, NoSuchMethodException, BindException {
        MutableCallSite mc = new DynamicCallSite(type);
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

        MethodHandle mh = makeFallBack(mc, caller, type, name, it);
        mc.setTarget(mh);

        return mc;
    }

    public static MethodHandle makeFallBack(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, INVOKE_TYPE it) {
        System.out.println("calculate target");
        MethodHandle mh = MethodHandles.insertArguments(it.getHandler(), 0, mc, caller, type, name);
        mh = mh.asCollector(Object[].class, type.parameterCount())
                .asType(type);
        return mh;
    }

    private static Object fieldGetProxy(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) throws Throwable {
        //[TODO] Selector
        DynamicSelector selector = DynamicSelector.getSelector(mc, caller, type, name, arguments, INVOKE_TYPE.GET);
        try {
            selector.setCallSite();
        } catch (BindException e) {
            name = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
            return invokeProxy(mc, caller, type, name, arguments);
        }
        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));
        return call.invokeExact(arguments);
    }

    private static Object fieldSetProxy(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) throws Throwable {
        //[TODO] Selector
        DynamicSelector selector = DynamicSelector.getSelector(mc, caller, type, name, arguments, INVOKE_TYPE.SET);
        try {
            selector.setCallSite();
        } catch (BindException e) {
            name = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
            return invokeProxy(mc, caller, type, name, arguments);
        }
        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));
        return call.invokeExact(arguments);
    }

    private static Object invokeProxy(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) throws Throwable {
        //[TODO] Selector
        DynamicSelector selector = DynamicSelector.getSelector(mc, caller, type, name, arguments, INVOKE_TYPE.METHOD);
        selector.setCallSite();
        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));
        return call.invokeExact(arguments);
    }

    public enum INVOKE_TYPE {
        GET("getField", FIELD_GET),
        SET("setField", FIELD_SET),
        METHOD("invoke", INVOKE_METHOD);
        private final String type;
        private final MethodHandle mh;

        private INVOKE_TYPE(String type, MethodHandle mh) {
            this.type = type;
            this.mh = mh;
        }

        public MethodHandle getHandler() {
            return mh;
        }
    }

}