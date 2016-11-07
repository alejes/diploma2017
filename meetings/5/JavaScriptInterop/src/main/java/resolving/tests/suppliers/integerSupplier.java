package resolving.tests.suppliers;

public interface integerSupplier {
    default Integer getInteger() {
        return 57;
    }
}

