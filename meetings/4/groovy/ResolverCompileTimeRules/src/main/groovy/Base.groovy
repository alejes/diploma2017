import groovy.transform.CompileStatic

@CompileStatic
public class Base {
}

@CompileStatic
public class Derived extends Base {}

@CompileStatic
public class Derived2lvl extends Derived {}