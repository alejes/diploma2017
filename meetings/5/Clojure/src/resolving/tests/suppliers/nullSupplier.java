package resolving.tests.suppliers;

import javax.lang.model.type.NullType;

public interface nullSupplier {
    default NullType getNull() {
        return null;
    }
}