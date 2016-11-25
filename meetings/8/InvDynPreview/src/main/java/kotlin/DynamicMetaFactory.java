package kotlin;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.BindException;
import java.util.*;

public class DynamicMetaFactory {
    private static final MethodHandles.Lookup DYNAMIC_LOOKUP = MethodHandles.lookup();
    private static final MethodHandle FIELD_GET;

    static {
        MethodType mt = MethodType.methodType(Object.class, MutableCallSite.class, MethodHandles.Lookup.class, MethodType.class, String.class, Object[].class);
        try {
            FIELD_GET = DYNAMIC_LOOKUP.findStatic(DynamicMetaFactory.class, "fieldGetProxy", mt);
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
        MutableCallSite mc = new MutableCallSite(type);
        MethodHandle mh = makeFallBack(mc, caller, type, name);
        mc.setTarget(mh);

        return mc;
    }

    private static MethodHandle makeFallBack(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name) {
        MethodHandle mh = MethodHandles.insertArguments(FIELD_GET, 0, mc, caller, type, name);
        mh = mh.asCollector(Object[].class, type.parameterCount())
                .asType(type);
        return mh;
    }

    private static Object fieldGetProxy(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) {
        return new Object();
    }

}
