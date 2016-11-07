package resolving.tests.suppliers;

public interface stringSupplier {
    default String  getString() {
        return "kon";
    }
}

