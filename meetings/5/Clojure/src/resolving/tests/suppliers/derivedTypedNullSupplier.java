package resolving.tests.suppliers;

import resolving.tests.Derived;

public interface derivedTypedNullSupplier {
    default Derived getDerivedTypedNull() {
        return (Derived) null;
    }
}