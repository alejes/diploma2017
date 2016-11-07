package resolving.tests.suppliers;

import resolving.tests.Derived;

public interface derivedNullSupplier {
    default Derived getDerivedNull() {
        return null;
    }
}