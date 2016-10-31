import groovy.transform.CompileStatic

/**
 * Created by user on 10/28/16.
 */
@CompileStatic
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

@CompileStatic
trait integerSupplier {
    def getInteger() {
        return 57;
    }
}

@CompileStatic
trait intSupplier {
    def getInt() {
        return 5;
    }
}

@CompileStatic
trait stringSupplier {
    def getString() {
        return "kon";
    }
}

@CompileStatic
trait doubleSupplier {
    def getDouble() {
        return 3.1493271828;
    }
}

@CompileStatic
trait shortSupplier {
    def getShort() {
        return 3223;
    }
}

@CompileStatic
trait baseSupplier {
    def getBase() {
        return new Base();
    }
}

@CompileStatic
trait derivedSupplier {
    def getDerived() {
        return new Derived();
    }
}

@CompileStatic
trait derived2lvlSupplier {
    def getDerived2lvl() {
        return new Derived2lvl();
    }
}

@CompileStatic
trait nullSupplier {
    def getNull() {
        return null;
    }
}

@CompileStatic
trait derivedNullSupplier {
    Derived getDerivedNull() {
        return null;
    }
}

@CompileStatic
trait derivedTypedNullSupplier {
    Derived getDerivedTypedNull() {
        return (Derived) null;
    }
}

@CompileStatic
class TraitSupplier implements integerSupplier,
        intSupplier, stringSupplier, doubleSupplier,
        shortSupplier, baseSupplier, derived2lvlSupplier,
        derivedSupplier, nullSupplier, derivedNullSupplier,
        derivedTypedNullSupplier {
}
