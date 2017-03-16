package kotlin;

import kotlin.text.StringsKt;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.*;
import java.net.BindException;
import java.util.HashMap;
import java.util.Map;


public class DynamicMetaFactory {
    /*
     * Method handles for guards
     */
    /* package */ static final MethodHandle IS_INSTANCE, IS_NULL;
    private static final MethodType
            CLASS_INSTANCE_MTYPE = MethodType.methodType(boolean.class, Class.class, Object.class),
            OBJECT_TEST_MTYPE = MethodType.methodType(boolean.class, Object.class);
    private static final MethodHandles.Lookup DYNAMIC_LOOKUP = MethodHandles.lookup();
    private static final MethodHandle FIELD_GET, FIELD_SET, INVOKE_METHOD;
    private static final Map<String, String> ASSIGNMENT_OPERATION_COUNTERPARTS = new HashMap<>();
    private static AssignmentMarker ASSIGNMENT_MARKER = new AssignmentMarker();

    static {
        MethodType mt = MethodType.methodType(Object.class, MutableCallSite.class, MethodHandles.Lookup.class, MethodType.class, String.class, Object[].class);
        MethodType mtInvoke = MethodType.methodType(Object.class, MutableCallSite.class, MethodHandles.Lookup.class, MethodType.class, String.class, Object[].class, String[].class);
        try {
            FIELD_GET = DYNAMIC_LOOKUP.findStatic(DynamicMetaFactory.class, "fieldGetProxy", mt);
            FIELD_SET = DYNAMIC_LOOKUP.findStatic(DynamicMetaFactory.class, "fieldSetProxy", mt);
            INVOKE_METHOD = DYNAMIC_LOOKUP.findStatic(DynamicMetaFactory.class, "invokeProxy", mtInvoke);
            IS_INSTANCE = DYNAMIC_LOOKUP.findStatic(DynamicGuards.class, "isInstance", CLASS_INSTANCE_MTYPE);
            IS_NULL = DYNAMIC_LOOKUP.findStatic(DynamicGuards.class, "isNull", OBJECT_TEST_MTYPE);
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
    static MethodHandle makeFallBack(MutableCallSite mc,
                                     MethodHandles.Lookup caller,
                                     MethodType type,
                                     String name,
                                     String[] namedArguments,
                                     INVOKE_TYPE it) {
        MethodHandle mh = MethodHandles.insertArguments(it.getHandler(), 0, mc, caller, type, name);
        if (it == INVOKE_TYPE.METHOD) {
            mh = MethodHandles.insertArguments(mh, 1, new Object[]{namedArguments});
        }
        mh = mh.asCollector(Object[].class, type.parameterCount())
                .asType(type);
        return mh;
    }

    private static Object fieldGetProxy(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) throws Throwable {
        //[TODO] Selector
        DynamicSelector selector = DynamicSelector.getFieldSelector(mc, caller, type, name, arguments, INVOKE_TYPE.GET);
        if (!selector.setCallSite()) {
            name = "get" + StringsKt.capitalize(name);
            return invokeProxy(mc, caller, type, name, arguments, null);
        }
        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));
        return call.invokeExact(arguments);
    }

    private static Object fieldSetProxy(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) throws Throwable {
        //[TODO] Selector
        DynamicSelector selector = DynamicSelector.getFieldSelector(mc, caller, type, name, arguments, INVOKE_TYPE.SET);
        if (!selector.setCallSite()) {
            name = "set" + StringsKt.capitalize(name);
            return invokeProxy(mc, caller, type, name, arguments, null);
        }
        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));
        return call.invokeExact(arguments);
    }

    private static Object invokeProxy(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments, @Nullable String[] namedArguments) throws Throwable {
        //[TODO] Selector
        boolean assignmentOperatorConversion = false;
        DynamicSelector selector = DynamicSelector.getMethodSelector(mc, caller, type, name, arguments, namedArguments);
        String operatorCounterpart = ASSIGNMENT_OPERATION_COUNTERPARTS.get(name);
        if (!selector.setCallSite()) {
            if (operatorCounterpart == null) {
                throw new DynamicBindException("Runtime: cannot find target method " + name);
            }
            assignmentOperatorConversion = true;
            selector.changeName(operatorCounterpart);
            if (!selector.setCallSite()) {
                throw new DynamicBindException("Runtime: cannot find target method " + name);
            }
        }
        MethodHandle call = selector.getMethodHandle()
                .asSpreader(Object[].class, arguments.length)
                .asType(MethodType.methodType(Object.class, Object[].class));

        Object result = call.invokeExact(arguments);

        if ((operatorCounterpart != null) && (!assignmentOperatorConversion)) {
            return ASSIGNMENT_MARKER;
        }

        return result;
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

    public static class AssignmentMarker {
        private AssignmentMarker() {
        }
    }
}
