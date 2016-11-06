package resolving.tests.suppliers;


public interface doubleSupplier {
    default double getDouble() {
        return 3.1493271828;
    }
}
