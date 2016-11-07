package resolving.tests.suppliers;

import resolving.tests.Base;

public interface baseSupplier {
    default Base getBase() {
        return new Base();
    }
}