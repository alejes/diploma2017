package resolving.tests.suppliers;

public interface shortSupplier {
    default short getShort() {
        return 3223;
    }
}
