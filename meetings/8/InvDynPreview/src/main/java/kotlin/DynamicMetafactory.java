package kotlin;


import java.lang.invoke.*;
import java.net.BindException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
            OBJECT_TRANSFORM_MTYPE = MethodType.methodType(Object.class, Object.class);
    private static final MethodHandles.Lookup DYNAMIC_LOOKUP = MethodHandles.lookup();
    private static final MethodHandle FIELD_GET, FIELD_SET, INVOKE_METHOD;
    private static final Map<String, String> ASSIGNMENT_OPERATION_COUNTERPARTS = new HashMap<>();
    private static final int CALLSITE_CACHE_SIZE = 5;

    static {
        MethodType mt = MethodType.methodType(Object.class,
                DynamicCallSite.class,
                MethodHandles.Lookup.class,
                MethodType.class,
                String.class,
                Object[].class);
        MethodType mtInvoke = MethodType.methodType(Object.class,
                DynamicCallSite.class,
                MethodHandles.Lookup.class,
                MethodType.class,
                String.class,
                Object[].class,
                int.class,
                String[].class,
                boolean.class);
        try {
            FIELD_GET = DYNAMIC_LOOKUP.findStatic(DynamicMetafactory.class, "fieldGetProxy", mt);
            FIELD_SET = DYNAMIC_LOOKUP.findStatic(DynamicMetafactory.class, "fieldSetProxy", mt);
            INVOKE_METHOD = DYNAMIC_LOOKUP.findStatic(DynamicMetafactory.class, "invokeProxy", mtInvoke);
            IS_INSTANCE = DYNAMIC_LOOKUP.findStatic(DynamicGuards.class, "isInstance", CLASS_INSTANCE_MTYPE);
            IS_NULL = DYNAMIC_LOOKUP.findStatic(DynamicGuards.class, "isNull", OBJECT_TEST_MTYPE);
            FILTER_UNIT = DYNAMIC_LOOKUP.findStatic(DynamicFilters.class, "returnUnit", OBJECT_TRANSFORM_MTYPE);
            FILTER_COMPOUND_ASSIGNMENT = DYNAMIC_LOOKUP.findStatic(DynamicFilters.class, "returnCompoundAssignmentPerformMarker", OBJECT_TRANSFORM_MTYPE);
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
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("modAssign", "mod");
        ASSIGNMENT_OPERATION_COUNTERPARTS.put("remAssign", "rem");
    }

    @SuppressWarnings("unused")
    public static CallSite bootstrapDynamic(MethodHandles.Lookup caller,
                                            String query,
                                            MethodType type,
                                            String name,
                                            int flags,
                                            String... namedArguments)
            throws IllegalAccessException, NoSuchMethodException, BindException {
        DynamicCallSite mc = new DynamicCallSite(type);
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

        MethodHandle mh = makeFallBack(mc, caller, type, name, flags, namedArguments, it);
        mc.setTarget(mh);

        return mc;
    }

    /* package */
    static MethodHandle makeFallBack(DynamicCallSite mc,
                                     MethodHandles.Lookup caller,
                                     MethodType type,
                                     String name,
                                     int flags,
                                     String[] namedArguments,
                                     InvokeType it) {
        MethodHandle mh = MethodHandles.insertArguments(it.getHandler(), 0, mc, caller, type, name);
        if (it == InvokeType.METHOD) {
            mh = MethodHandles.insertArguments(mh, 1, flags, namedArguments, /* allowNamingConversion */true);
        }
        mh = mh.asCollector(Object[].class, type.parameterCount())
                .asType(type);
        return mh;
    }

    @SuppressWarnings("unused")
    private static Object fieldGetProxy(DynamicCallSite mc,
                                        MethodHandles.Lookup caller,
                                        MethodType type,
                                        String name,
                                        Object[] arguments) throws Throwable {
        MethodHandle targetMethodHandle = mc.methodHandleCache.get(arguments);

        if (targetMethodHandle == null) {
            //[TODO] Selector
            DynamicSelector selector = DynamicSelector.getFieldSelector(mc, caller, type, name, arguments, InvokeType.GET);
            if (!selector.setCallSite()) {
                throw new DynamicBindException("Cannot find getter for field " + name);
            }

            targetMethodHandle = selector.getMethodHandle()
                    .asSpreader(Object[].class, arguments.length)
                    .asType(MethodType.methodType(Object.class, Object[].class));

            MethodHandleEntry methodHandleEntry = MethodHandleEntry.buildFromArguments(arguments);
            mc.methodHandleCache.put(methodHandleEntry, targetMethodHandle);
        }
        return targetMethodHandle.invokeExact(arguments);
    }

    @SuppressWarnings("unused")
    private static Object fieldSetProxy(DynamicCallSite mc,
                                        MethodHandles.Lookup caller,
                                        MethodType type,
                                        String name,
                                        Object[] arguments) throws Throwable {
        MethodHandle targetMethodHandle = mc.methodHandleCache.get(arguments);

        if (targetMethodHandle == null) {
            //[TODO] Selector
            DynamicSelector selector = DynamicSelector.getFieldSelector(mc, caller, type, name, arguments, InvokeType.SET);
            if (!selector.setCallSite()) {
                throw new DynamicBindException("Cannot find setter for field " + name);
            }

            targetMethodHandle = selector.getMethodHandle()
                    .asSpreader(Object[].class, arguments.length)
                    .asType(MethodType.methodType(Object.class, Object[].class));

            MethodHandleEntry methodHandleEntry = MethodHandleEntry.buildFromArguments(arguments);
            mc.methodHandleCache.put(methodHandleEntry, targetMethodHandle);
        }
        return targetMethodHandle.invokeExact(arguments);
    }

    @SuppressWarnings("unused")
    private static Object invokeProxy(DynamicCallSite mc,
                                      MethodHandles.Lookup caller,
                                      MethodType type,
                                      String name,
                                      Object[] arguments,
                                      int flags,
                                      /* Nullable */ String[] namedArguments,
                                      boolean allowNamingConversion
    ) throws Throwable {
        MethodHandle targetMethodHandle = mc.methodHandleCache.get(arguments);

        if (targetMethodHandle == null) {
            //[TODO] Selector
            DynamicSelector selector = DynamicSelector.getMethodSelector(mc, caller, type, name, arguments, flags, namedArguments);
            String operatorCounterpart = ASSIGNMENT_OPERATION_COUNTERPARTS.get(name);
            boolean hasCounterpart = operatorCounterpart != null;
            boolean callSiteMounted = selector.setCallSite(hasCounterpart);
            if (allowNamingConversion && !callSiteMounted) {
                if (operatorCounterpart != null) {
                    selector.changeName(operatorCounterpart);
                    callSiteMounted = selector.setCallSite();
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

            targetMethodHandle = selector.getMethodHandle()
                    .asSpreader(Object[].class, arguments.length)
                    .asType(MethodType.methodType(Object.class, Object[].class));

            MethodHandleEntry methodHandleEntry = MethodHandleEntry.buildFromArguments(arguments);
            mc.methodHandleCache.put(methodHandleEntry, targetMethodHandle);
        }

        return targetMethodHandle.invokeExact(arguments);
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

        private final String type;
        private final String javaPrefix;
        private final MethodHandle mh;

        private InvokeType(String type,
                           String javaPrefix,
                           MethodHandle mh) {
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

    /* package */ static final class DynamicCallSite extends MutableCallSite {
        /* package */ CacheMap methodHandleCache = new CacheMap();

        /* package */ DynamicCallSite(MethodType type) {
            super(type);
        }
    }

    private static final class CacheMap {
        private final LinkedList<CacheMap.Entry> list = new LinkedList<>();

        /* package */
        synchronized MethodHandle get(MethodHandleEntry entry) {
            Iterator<Entry> iterator = list.iterator();
            int hash = entry.hash;
            while (iterator.hasNext()) {
                Entry e = iterator.next();
                if ((e.key.hashCode() == hash) && (e.key.equals(entry))) {
                    iterator.remove();
                    list.addFirst(e);
                    return e.value;
                }
            }
            return null;
        }

        /* package */
        synchronized MethodHandle get(Object[] entry) {
            int hash = MethodHandleEntry.computeHashCode(entry);
            //listLoop:
            for (Entry e : list) {
                if ((e.key.hashCode() == hash) && e.key.objectEquals(entry)) {
                    /*Class[] arguments = e.key.argumentClasses;
                    if (arguments.length != entry.length) {
                        continue;
                    }

                    for (int i = 0; i < arguments.length; ++i) {
                        if (entry[i].getClass() != arguments[i]) {
                            break listLoop;
                        }
                    }*/
                    //iterator.remove();
                    //list.addFirst(e);
                    return e.value;
                }
            }
            return null;
        }

        /* package */
        synchronized void put(MethodHandleEntry key, MethodHandle value) {
            list.addFirst(new Entry(key, value));
            if (list.size() > CALLSITE_CACHE_SIZE) {
                list.removeLast();
            }
        }

        /* package */ static final class Entry {
            /* package */ final MethodHandleEntry key;
            /* package */ final MethodHandle value;

            /* package */ Entry(MethodHandleEntry key, MethodHandle value) {
                this.key = key;
                this.value = value;
            }
        }
    }

    private static final class MethodHandleEntry {
        /* package */ final Class[] argumentClasses;
        private final int hash;

        /* package */ MethodHandleEntry(Class[] argumentClasses) {
            this.argumentClasses = argumentClasses;
            int tempHash = 0;
            for (Class clazz : argumentClasses) {
                tempHash ^= clazz.hashCode();
            }
            hash = tempHash;
        }


        /* package */
        static MethodHandleEntry buildFromArguments(Object[] argumentClasses) {
            Class[] clazzArray = new Class[argumentClasses.length];
            for (int i = 0; i < argumentClasses.length; ++i) {
                Object obj = argumentClasses[i];
                if (obj == null) {
                    clazzArray[i] = void.class;
                } else {
                    clazzArray[i] = argumentClasses[i].getClass();
                }
            }
            return new MethodHandleEntry(clazzArray);
        }

        /* package */
        static int computeHashCode(Object[] args) {
            int hash = 0;
            for (Object obj : args) {
                if (obj == null) {
                    hash ^= void.class.hashCode();
                } else {
                    hash ^= obj.getClass().hashCode();
                }
            }
            return hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }

        /* package */
        boolean objectEquals(Object[] objects) {
            if (objects.length != argumentClasses.length) {
                return false;
            }
            for (int i = 0; i < argumentClasses.length; ++i) {
                if (argumentClasses[i] != objects[i].getClass())
                    return false;
            }
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;

            if (obj.getClass() != MethodHandleEntry.class)
                return false;

            Class[] objClasses = ((MethodHandleEntry) obj).argumentClasses;
            if (objClasses.length != argumentClasses.length) {
                return false;
            }

            for (int i = 0; i < argumentClasses.length; ++i) {
                if (argumentClasses[i] != objClasses[i])
                    return false;
            }

            return true;
        }
    }

    /* package */ static final class ObjectInvoker {
        private final MethodHandle getterCall;
        private final MethodHandles.Lookup caller;
        /* Nullable */
        private final String[] namedArguments;
        private final boolean isReturnUnit;
        private final ThreadLocal<MethodHandle> cachedCall;
        private final ThreadLocal<Class[]> cachedArguments;

        /* package */ ObjectInvoker(MethodHandle getterCall,
                                    MethodHandles.Lookup caller,
                                    /* Nullable */ String[] namedArguments) {
            this(getterCall, caller, namedArguments, null, new Class[]{});
        }

        /* package */ ObjectInvoker(MethodHandle getterCall,
                                    MethodHandles.Lookup caller,
                                    /* Nullable */ String[] namedArguments,
                                    /* Nullable */ MethodHandle cachedCall,
                                    Class[] cachedArguments) {
            this.getterCall = getterCall;
            this.caller = caller;
            this.namedArguments = namedArguments;
            this.cachedCall = ThreadLocal.withInitial(() -> cachedCall);
            this.cachedArguments = ThreadLocal.withInitial(() -> cachedArguments);
            this.isReturnUnit = getterCall.type().returnType().equals(void.class);
        }

        private boolean checkCache(/* Nullable */ MethodHandle cachedCall, Class[] cachedArguments, Object[] arguments) {
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
