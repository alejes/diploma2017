/**
 * Created by user on 10/28/16.
 */
class Supplier {
    def getInt() {
        return (int) 5;
    }

    def getInteger() {
        return (Integer) 57;
    }

    def getString() {
        return "kon";
    }

    def getDouble() {
        return 3.1493271828;
    }

    def getShort() {
        return 3223;
    }
}


trait integerSupplier {
    def getInteger() {
        return 57;
    }
}

trait intSupplier {
    def getInt() {
        return 5;
    }
}

trait stringSupplier {
    def getString() {
        return "kon";
    }
}

trait doubleSupplier {
    def getDouble() {
        return 3.1493271828;
    }
}

trait shortSupplier {
    def getShort() {
        return 3223;
    }
}

trait baseSupplier {
    def getBase() {
        return new Base();
    }
}

trait derivedSupplier {
    def getDerived() {
        return new Derived();
    }
}

trait derived2lvlSupplier {
    def getDerived2lvl() {
        return new Derived2lvl();
    }
}

trait nullSupplier {
    def getNull() {
        return null;
    }
}


trait derivedNullSupplier {
    Derived getDerivedNull() {
        return null;
    }
}

trait derivedTypedNullSupplier {
    Derived getDerivedTypedNull() {
        return (Derived) null;
    }
}

class TraitSupplier implements integerSupplier,
        intSupplier, stringSupplier, doubleSupplier,
        shortSupplier, baseSupplier, derived2lvlSupplier,
        derivedSupplier, nullSupplier, derivedNullSupplier,
        derivedTypedNullSupplier {
}

;