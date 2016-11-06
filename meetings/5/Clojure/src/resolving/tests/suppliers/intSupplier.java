package resolving.tests.suppliers;

public interface intSupplier {
    default  int getInt() {
        return 5;
    }
}