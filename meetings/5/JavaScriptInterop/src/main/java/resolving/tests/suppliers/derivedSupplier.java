package resolving.tests.suppliers;

import resolving.tests.Derived;

public interface derivedSupplier {
    default Derived getDerived() {
        return new Derived();
    }
}