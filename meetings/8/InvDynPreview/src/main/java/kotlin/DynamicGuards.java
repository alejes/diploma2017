package kotlin;

public final class DynamicGuards {
    public static boolean isInstance(Class c, Object o) {
        return o != null && o.getClass() == c;
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

}
