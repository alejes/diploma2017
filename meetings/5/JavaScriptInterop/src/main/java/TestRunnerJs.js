function assertEquals(actual, expected, isPrinted) {
    if (typeof(isPrinted) === 'undefined') isPrinted = true;

    if (isPrinted) {
        print("Result = ", actual, "[", actual == expected, "]")
    }
    else {
        if (actual != expected) {
            var e = new Error('dummy');
            var stack = e.stack.replace(/^[^\(]+?[\n$]/gm, '')
                .replace(/^\s+at\s+/gm, '')
                .replace(/^Object.<anonymous>\s*\(/gm, '{anonymous}()@')
                .split('\n');
            throw "Assertion failed, actual=" + actual + "\n" + stack;
        }
    }
}

function println(text) {
    print(text)
}



var Supplier = Java.type('resolving.tests.Supplier');
var BasicRouter = Java.type('resolving.tests.BasicRouter');
var TraitSupplier = Java.type('resolving.tests.DefaultInterface_Trait_Supplier');
var AmbiguousRouter = Java.type('resolving.tests.AmbiguousRouter');
var InterfaceSupplier = Java.type('resolving.tests.InterfaceSupplier');
var Derived = Java.type('resolving.tests.Derived');
var IntegerClass = Java.type('java.lang.Integer');

var x = 1;
var br = new BasicRouter();
print("Обычное разрешение");

assertEquals(br.method_(x), 4);
assertEquals(br.method_("one"), 0);
assertEquals(br.method_(29), 2);
/*diff*/
assertEquals(br.method_((parseInt(29, 10))), 2);
assertEquals(br.method_((new IntegerClass(29))), 4);
assertEquals(br.method_(2.9), 2);
/*diff*/
assertEquals(br.method_(parseFloat(2.9)), 2);
assertEquals(br.method_(parseFloat("2.9")), 2);

println("Integer");
assertEquals(br.method_IntDyn(x), 0);

println("Supplier");

usualSupplier = new Supplier();
assertEquals(br.method_(usualSupplier.getInt()), 4);
assertEquals(br.method_(parseInt(usualSupplier.getInt(), 10)), 2);
assertEquals(br.method_(usualSupplier.getString()), 0);
assertEquals(br.method_(usualSupplier.getInteger()), 4);
assertEquals(br.method_(usualSupplier.getDouble()), 2);
/* diff*/


println("Supplier with trait");
traitSupplier = new TraitSupplier();
assertEquals(br.method_(traitSupplier.getInt()), 4);
assertEquals(br.method_(parseInt(traitSupplier.getInt(), 10)), 2);
assertEquals(br.method_(traitSupplier.getString()), 0);
assertEquals(br.method_(traitSupplier.getInteger()), 4);
assertEquals(br.method_(traitSupplier.getDouble()), 2);
/*diff*/


println("Inheritance without integer");
traitSupplier = new TraitSupplier();
assertEquals(br.methodNoInteger__(traitSupplier.getBase(), traitSupplier.getInt()), 0);
assertEquals(br.methodNoInteger__(traitSupplier.getBase(), traitSupplier.getInteger()), 0);
was = false;
try {
    assertEquals(br.methodNoInteger__(traitSupplier.getDerived(), traitSupplier.getInteger()), 1);
}
catch (exc) {
    was = true;
    println(exc);
    /*java.lang.NoSuchMethodException: Can't unambiguously select between fixed arity signatures
     [(resolving.tests.Derived, short), (resolving.tests.Base, int)]
     of the method resolving.tests.BasicRouter.methodNoInteger__ for argument types
     [resolving.tests.Derived, java.lang.Integer]*/
}
assertEquals(was, true);
assertEquals(br.methodNoInteger__(traitSupplier.getDerived2lvl(), traitSupplier.getInteger()), 5);

println("Inheritance with integer");
traitSupplier = new TraitSupplier();
assertEquals(br.method__(traitSupplier.getBase(), traitSupplier.getInt()), 0);
assertEquals(br.method__(traitSupplier.getBase(), traitSupplier.getInteger()), 0);
was = false;
try {
    assertEquals(br.method__(traitSupplier.getDerived(), traitSupplier.getInteger()), 4);
}
catch (exc) {
    was = true;
    println(exc);
    /*
     Sometimes:
     java.lang.NoSuchMethodException: Can't unambiguously select between fixed arity signatures
     [(resolving.tests.Derived, java.lang.Integer), (resolving.tests.Base, int)]
     of the method resolving.tests.BasicRouter.method__ for argument types
     [resolving.tests.Derived, java.lang.Integer]
     */
}
println("Time Exception: " + was);
assertEquals(was, was);


println("Supplier with Null");

was = false;
try {
    assertEquals(br.method_(traitSupplier.getNull()), 1);
}
catch (exc) {
    was = true;
    println("ok");
    println(exc);
    /*Sometimes!!!*/
    /*java.lang.NoSuchMethodException: Can't unambiguously select between fixed arity signatures
     [(resolving.tests.Derived, short), (resolving.tests.Base, int)]
     of the method resolving.tests.BasicRouter.methodNoInteger__ for argument types
     [resolving.tests.Derived, java.lang.Integer]*/
}
print("Check " + was);
assertEquals(was, true);
assertEquals(br.method_(traitSupplier.getDerivedNull()), 1);
/*assertEquals(br.method_((Derived)(traitSupplier.getDerivedNull())), 6);*/
assertEquals(br.method_(traitSupplier.getDerivedTypedNull()), 1);


println("Ambiguous Interface Router");
ar = new AmbiguousRouter();
is = new InterfaceSupplier();
assertEquals(ar.method_IP12(is.getInterfaceProvider()), 1);
was = false;
try {
    assertEquals(ar.method_I12(is.getInterfaceProvider()), 2);
}
catch (re) {
    was = true;
    println(re);
    /*
     java.lang.NoSuchMethodException: Can't unambiguously select between fixed arity signatures
     [(resolving.tests.I2), (resolving.tests.I1)] of the method
     resolving.tests.AmbiguousRouter.method_I12 for argument types [resolving.tests.InterfaceProvider]
     */
}
assertEquals(was, true);


println("Default Router not supported on JVM");