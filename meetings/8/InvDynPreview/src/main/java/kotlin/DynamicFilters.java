package kotlin;

@SuppressWarnings("unused")
public final class DynamicFilters {
    public static Object returnUnit(Object oldRes) {
        return Unit.INSTANCE;
    }

    public static Object returnCompoundAssignmentPerformMarker(Object oldRes) {
        return DynamicMetafactory.COMPOUND_ASSIGNMENT_PERFORM_MARKER;
    }
}
