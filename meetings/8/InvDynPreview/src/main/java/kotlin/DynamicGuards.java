package kotlin;

public final class DynamicGuards {
    public static boolean isInstance(Class c, Object o) {
        return o != null && o.getClass() == c;
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static boolean isReferencesEqual(Object o1, Object o2) {
        System.out.println(o1.toString() + ":" + o2.toString());
        return o1 == o2;
    }
}
