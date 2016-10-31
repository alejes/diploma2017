/**
 * Created by Alexey on 31.10.2016.
 */
class DefaultRouter {
    def method__(arg1) {
        return 1
    }
    def method__(Base arg1, Integer x = 4) {
        return 2
    }
}

/*
class DefaultRouterTemplate {
    def method__(arg1) {
        return 1
    }
    def method__(Base arg1, Integer x = 4) {
        return 2
    }
    def <T> T method__(T arg1) {
        return null;
    }
}
*/

