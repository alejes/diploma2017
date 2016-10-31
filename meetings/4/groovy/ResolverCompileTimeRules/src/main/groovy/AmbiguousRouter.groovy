import groovy.transform.CompileStatic

@CompileStatic
class InterfaceProvider implements I1, I2 {
    def method1() {
        return 3
    }
}

@CompileStatic
class InterfaceSupplier {
    def getInterfaceProvider() {
        return new InterfaceProvider();
    }
}

@CompileStatic
interface I1 {
    def method1();
}

@CompileStatic
interface I2 {
    def method1();
}

@CompileStatic
public class AmbiguousRouter {
    def method_IP12(InterfaceProvider a) {
        return 1
    }

    def method_IP12(I1 a) {
        return 2
    }

    def method_IP12(I2 a) {
        return 3
    }

    def method_I12(I1 a) {
        return 2
    }

    def method_I12(I2 a) {
        return 3
    }
}
