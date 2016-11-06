package resolving.tests;

public class AmbiguousRouter {
    public int method_IP12(InterfaceProvider a) {
        return 1;
    }

    public int method_IP12(I1 a) {
        return 2;
    }

    public int method_IP12(I2 a) {
        return 3;
    }

    public int method_I12(I1 a) {
        return 2;
    }

    public int method_I12(I2 a) {
        return 3;
    }
}
