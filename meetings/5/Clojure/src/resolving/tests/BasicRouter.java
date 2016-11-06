package resolving.tests;

public class BasicRouter {
    public int method_IntDyn(int first) {
        return 0;
    }

    public int method_IntDyn(Object first) {
        return 1;
    }

    public int method_(String first) {
        return 0;
    }

    public int method_(Object first) {
        return 1;
    }

    public int method_(int first) {
        return 2;
    }

    public int method_(short second) {
        return 3;
    }

    public int method_(Integer first) {
        return 4;
    }

    public int method_(Base first) {
        return 5;
    }

    public int method_(Derived first) {
        return 6;
    }

    public int methodNoInteger__(Base first, int second) {
        return 0;
    }

    public int methodNoInteger__(Derived first, Object second) {
        return 1;
    }

    public int methodNoInteger__(Derived2lvl first, int second) {
        return 5;
    }

    public int methodNoInteger__(Derived2lvl first, String second) {
        return 2;
    }

    public int methodNoInteger__(Derived first, short second) {
        return 3;
    }

    public int method__(Base first, int second) {
        return 0;
    }

    public int method__(Derived first, Object second) {
        return 1;
    }

    public int method__(Derived2lvl first, String second) {
        return 2;
    }

    public int method__(Derived first, short second) {
        return 3;
    }

    public int method__(Derived first, Integer second) {
        return 4;
    }

    public int alwaysResolved(int x) {
        return x;
    }
}