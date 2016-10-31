/**
 * Created by user on 10/28/16.
 */
class BasicRouter {
    int method_IntDyn(int first) {
        return 0;
    }

    int method_IntDyn(first) { return 1; }

    int method_(String first) {
        return 0;
    }

    int method_(first) {
        return 1;
    }

    int method_(int first) {
        return 2;
    }

    int method_(short second) {
        return 3;
    }

    int method_(Integer first) {
        return 4;
    }

    int method_(Base first) {
        return 5;
    }

    int method_(Derived first) {
        return 6;
    }

    int methodNoInteger__(Base first, int second) {
        return 0;
    }

    int methodNoInteger__(Derived first, second) {
        return 1;
    }

    int methodNoInteger__(Derived2lvl first, int second) {
        return 5;
    }

    int methodNoInteger__(Derived2lvl first, String second) {
        return 2;
    }

    int methodNoInteger__(Derived first, short second) {
        return 3;
    }

    int method__(Base first, int second) {
        return 0;
    }

    int method__(Derived first, second) {
        return 1;
    }

    int method__(Derived2lvl first, String second) {
        return 2;
    }

    int method__(Derived first, short second) {
        return 3;
    }

    int method__(Derived first, Integer second) {
        return 4;
    }
}
