package kotlin;

public class DynamicGuards {
    public static boolean isInstance(Class c, Object o) {
        return o.getClass() == c;
    }
    public static boolean isNull(Object o) {
        return o == null;
    }
}
