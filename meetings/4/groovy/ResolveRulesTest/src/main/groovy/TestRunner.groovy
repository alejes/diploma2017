br = new BasicRouter()

int x = 1
println("Обычное разрешение")

assert br.method_(x) == 4
//int x --> Integer
assert br.method_("one") == 0
assert br.method_(29) == 4
assert br.method_((int) 29) == 2
assert br.method_((Integer) 29) == 4
assert br.method_(2.9) == 1

println("Integer")
assert br.method_IntDyn(x) == 0
assert br.method_IntDyn(x) == 0


println("Supplier")

usualSupplier = new Supplier();
assert br.method_(usualSupplier.getInt()) == 4
assert br.method_((int) usualSupplier.getInt()) == 2
assert br.method_(usualSupplier.getString()) == 0
assert br.method_(usualSupplier.getInteger()) == 4
assert br.method_(usualSupplier.getDouble()) == 1


println("Supplier with trait")
traitSupplier = new TraitSupplier();
assert br.method_(traitSupplier.getInt()) == 4
assert br.method_((int) traitSupplier.getInt()) == 2
assert br.method_(traitSupplier.getString()) == 0
assert br.method_(traitSupplier.getInteger()) == 4
assert br.method_(traitSupplier.getDouble()) == 1

println("Inheritance without integer")
traitSupplier = new TraitSupplier();
assert br.methodNoInteger__(traitSupplier.getBase(), traitSupplier.getInt()) == 0
assert br.methodNoInteger__(traitSupplier.getBase(), traitSupplier.getInteger()) == 0
assert br.methodNoInteger__(traitSupplier.getDerived(), traitSupplier.getInteger()) == 1
//^ method call ambiguous in Idea
// ^ unexpected
assert br.methodNoInteger__(traitSupplier.getDerived2lvl(), traitSupplier.getInteger()) == 5


println("Inheritance with integer")
traitSupplier = new TraitSupplier();
assert br.method__(traitSupplier.getBase(), traitSupplier.getInt()) == 0
assert br.method__(traitSupplier.getBase(), traitSupplier.getInteger()) == 0
// interesting, ambiguous for me
assert br.method__(traitSupplier.getDerived(), traitSupplier.getInteger()) == 4


println("Supplier with Null")
assert br.method_(traitSupplier.getNull()) == 1
assert br.method_(traitSupplier.getDerivedNull()) == 1
//typed null!
assert br.method_((Derived)traitSupplier.getDerivedNull()) == 6
assert br.method_(traitSupplier.getDerivedTypedNull()) == 1

println("Ambiguous Interface Router")
ar = new AmbiguousRouter()
is = new InterfaceSupplier()
assert ar.method_IP12(is.getInterfaceProvider()) == 1
wasException  = false
try {
    assert ar.method_I12(is.getInterfaceProvider()) == 2
//^ method call ambiguous in Idea
}
catch (GroovyRuntimeException gre) {
    wasException = true
    println gre.message
    /*
Caught: groovy.lang.GroovyRuntimeException: Ambiguous method overloading for method AmbiguousRouter#method_I12.
Cannot resolve which method to invoke for [class InterfaceProvider] due to overlapping prototypes between:
	[interface I1]
	[interface I2]
 */
}
assert wasException


println("Default Router")
dr = new DefaultRouter()
assert dr.method__(traitSupplier.getDerived()) == 2
assert dr.method__(traitSupplier.getBase()) == 2

