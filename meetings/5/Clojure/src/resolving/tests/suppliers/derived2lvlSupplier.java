package resolving.tests.suppliers;

import resolving.tests.Derived2lvl;

/**
 * Created by Alexey on 06.11.2016.
 */
public interface derived2lvlSupplier {
    default Derived2lvl getDerived2lvl() {
        return new Derived2lvl();
    }
}