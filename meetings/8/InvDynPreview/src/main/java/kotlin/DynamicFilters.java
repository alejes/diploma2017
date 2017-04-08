package kotlin;

@SuppressWarnings("unused")
public class DynamicFilters {
    public static Object returnUnit() {
        return Unit.INSTANCE;
    }

    public static Object returnCompoundAssignmentPerformMarker() {
        return DynamicMetafactory.COMPOUND_ASSIGNMENT_PERFORM_MARKER;
    }
}
