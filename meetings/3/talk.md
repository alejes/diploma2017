Intro
### C#

[Own DynamicObject Like Scala](https://msdn.microsoft.com/en-us/library/system.dynamic.dynamicobject(v=vs.110).aspx)

`
The type dynamic has special meaning in C#. Its purpose is to allow dynamic binding, which is described in detail in §7.2.2.
dynamic is considered identical to object except in the following respects:

- Operations on expressions of type dynamic can be dynamically bound (§7.2.2).
- __Type inference (§7.5.2) will prefer dynamic over object if both are candidates.__
 
Because of this equivalence, the following holds:

- There is an implicit identity conversion between object and dynamic, and between constructed types that are the same when replacing dynamic with object
- Implicit and explicit conversions to and from object also apply to and from dynamic.
- __Method signatures that are the same when replacing dynamic with object are considered the same signature__
- 
__The type dynamic is indistinguishable from object at run-time.__

An expression of the type dynamic is referred to as a dynamic expression.


### My overview

- If we have a function member that has as receiver or argument of a dynamic type, at compilation, we only do the checks in accordance with 7.5.4 Compile-time checking of dynamic overload resolution.
- At runtime if we see such method then in runtime we define a set of candidate function members using the rules described in 7.5.3 Overloading resolution:
  - Invocation of a method named in an invocation-expression (§7.6.5.1).
  - Invocation of an instance constructor named in an object-creation-expression (§7.6.10.1).
Invocation of an indexer accessor through an element-access (§7.6.6).
Invocation of a predefined or user-defined operator referenced in an expression (§7.3.3 and §7.3.4).
After using exactly the same rules as compile-time (7.5.3.2 Better function member) in runtime we define Better function member. During the method definition, all the arguments which are of type dynamic is considered to have the type object. Uncertainties between Dynamic vs Object does not occur, because they are rejected at compile time.
Finally, according to the rules 7.5.3 Overloading resolution, we define the overload or fall with the exception if failed.




```
7.2.3 Types of constituent expressions

When an operation is statically bound, the type of a constituent expression (e.g. a receiver, and argument, an index or an operand) is always considered to be the compile-time type of that expression. When an operation is dynamically bound, the type of a constituent expression is determined in different ways depending on the compile-time type of the constituent expression:

• A constituent expression of compile-time type dynamic is considered to have the type of the actual value that the expression evaluates to at runtime

• A constituent expression whose compile-time type is a type parameter is considered to have the type which the type parameter is bound to at runtime

• Otherwise the constituent expression is considered to have its compile-time type.
```

[Documentation](http://dlr.codeplex.com/wikipage?title=Docs%20and%20specs&referringTitle=Documentation) [ [Short Intro](https://msdn.microsoft.com/en-us/library/dd233052(v=vs.110).aspx) ]

[Difference between dynamic and compile resolution](http://codinglight.blogspot.ru/2009/05/dynamic-type-and-runtime-overload.html)


### Overloading resolution 7.5.3
Overload resolution selects the function member to invoke in the following distinct contexts within C#:
    
- Invocation of a method named in an invocation-expression (§7.6.5.1).
- Invocation of an instance constructor named in an object-creation-expression (§7.6.10.1).
- Invocation of an indexer accessor through an element-access (§7.6.6).
- Invocation of a predefined or user-defined operator referenced in an expression (§7.3.3 and §7.3.4).

Each of these contexts defines the __set of candidate__ function members and the list of arguments in its own unique way, as described in detail in the sections listed above.

Once the candidate function members and the argument list have been identified, the selection of the best function member is the same in all cases:
- Given the set of applicable candidate function members, the best function member in that set is located. If the set contains only one function member, then that function member is the best function member. 
- Otherwise, the best function member is the one function member that is better than all other function members with respect to the given argument list, provided that each function member is compared to all other function members using the rules in __§7.5.3.2__. 
- -If there is not exactly one function member that is better than all other function members, then the function member invocation is ambiguous and a __binding-time__ error occurs.
- 
The following sections define the exact meanings of the terms _applicable function member_ and _better function member_.


### 7.5.3.2 Better function member
For the purposes of determining the better function member, a stripped-down argument list A is constructed containing just the argument expressions themselves in the order they appear in the original argument list.

Parameter lists for each of the candidate function members are constructed in the following way: 
- The expanded form is used if the function member was applicable only in the expanded form.
- Optional parameters with no corresponding arguments are removed from the parameter list
- The parameters are reordered so that they occur at the same position as the corresponding argument in the argument list.

Given an argument list A with a set of argument expressions { E1, E2, ..., EN } and two applicable function members MP and MQ with parameter types { P1, P2, ..., PN } and { Q1, Q2, ..., QN }, MP is defined to be a better function member than MQ if
- for each argument, the implicit conversion from EX to QX is not better than the implicit conversion from EX to PX, and
- for at least one argument, the conversion from EX to PX is better than the conversion from EX to QX.

When performing this evaluation, if MP or MQ is applicable in its expanded form, then PX or QX refers to a parameter in the expanded form of the parameter list.

In case the parameter type sequences {P1, P2, …, PN} and {Q1, Q2, …, QN} are equivalent (i.e. each Pi has an identity conversion to the corresponding Qi), the following tie-breaking rules are applied, in order, to determine the better function member. 
- If MP is a non-generic method and MQ is a generic method, then MP is better than MQ.
- Otherwise, if MP is applicable in its normal form and MQ has a params array and is applicable only in its expanded form, then MP is better than MQ.
- Otherwise, if MP has more declared parameters than MQ, then MP is better than MQ. This can occur if both methods have params arrays and are applicable only in their expanded forms.
- Otherwise if all parameters of MP have a corresponding argument whereas default arguments need to be substituted for at least one optional parameter in MQ then MP is better than MQ. 
- Otherwise, if MP has more specific parameter types than MQ, then MP is better than MQ. Let {R1, R2, …, RN} and {S1, S2, …, SN} represent the uninstantiated and unexpanded parameter types of MP and MQ. MP’s parameter types are more specific than MQ’s if, for each parameter, RX is not less specific than SX, and, for at least one parameter, RX is more specific than SX:
  -	A type parameter is less specific than a non-type parameter.
  -	Recursively, a constructed type is more specific than another constructed type (with the same number of type arguments) if at least one type argument is more specific and no type argument is less specific than the corresponding type argument in the other.
  -	An array type is more specific than another array type (with the same number of dimensions) if the element type of the first is more specific than the element type of the second.
-	Otherwise if one member is a non-lifted operator and  the other is a lifted operator, the non-lifted one is better.
-	Otherwise, neither function member is better.


### 7.5.4 Compile-time checking of dynamic overload resolution
For most dynamically bound operations the set of possible candidates for resolution is unknown at compile-time. In certain cases, however the candidate set is known at compile-time:
- Static method calls with dynamic arguments
- Instance method calls where the receiver is not a dynamic expression
- Indexer calls where the receiver is not a dynamic expression
- Constructor calls with dynamic arguments

In these cases a limited compile-time check is performed for each candidate to see if any of them could possibly apply at run-time.This check consists of the following steps:
- Partial type inference: Any type argument that does not depend directly or indirectly on an argument of type _dynamic_ is inferred using the rules of §7.5.2. The remaining type arguments are _unknown_.
- Partial applicability check: Applicability is checked according to §7.5.3.1, but ignoring parameters whose types are _unknown_.
    
If no candidate passes this test, a __compile-time__ error occurs.


### [Overload resolution with dynamic](https://blogs.msdn.microsoft.com/cburrows/2010/04/01/errata-dynamic-conversions-and-overload-resolution/)
The new rules are dead simple: __If you have a method call with a dynamic argument, it is dispatched dynamically, period.__ During the runtime binding, all the static types of your arguments are known, and __types are picked for the dynamic arguments based on their actual values__.

See? That’s much simpler to explain, and easier to reason about. It means that in the following code, the call to M will be dispatched at runtime, and the M that gets picked will depend on the value in d. In this case, M(string) will be called.

```
class C {
    void M(dynamic d) { }
    void M(string s) { }
    void M(int i) { }
    void CallM() {
        dynamic d = “test”;
        this.M(d);
    }
}
```
You might ask, what about that M(dynamic) overload? Well, when you have a parameter of type dynamic, the fact that it is dynamic is only relevant in the body of the method. Because of this new simple scheme for overload resolution, as callers see it, M(dynamic) is really no different than M(object).

### My overview on C# [Chris Burrows :(](https://github.com/cburrows)

- If we have a function member that has as receiver or argument of a dynamic type, at compilation, we only do the checks in accordance with 7.5.4 Compile-time checking of dynamic overload resolution.
- At runtime if we see such method then in runtime we define a set of candidate function members using the rules described in 7.5.3 Overloading resolution:
  - Invocation of a method named in an invocation-expression (§7.6.5.1).
  - Invocation of an instance constructor named in an object-creation-expression (§7.6.10.1).
  - Invocation of an indexer accessor through an element-access (§7.6.6).
  - Invocation of a predefined or user-defined operator referenced in an expression (§7.3.3 and §7.3.4).
- After using exactly the same rules as compile-time (7.5.3.2 Better function member) in runtime we define Better function member. During the method definition, all the arguments which are of type dynamic is considered to have the type object. Uncertainties between Dynamic vs Object does not occur, because they are rejected at compile time.
- Finally, according to the rules 7.5.3 Overloading resolution, we define the overload or fall with the exception if failed.


### Scala

http://docs.scala-lang.org/sips/completed/type-dynamic.html

```
import scala.language.dynamics

class DynTrait extends Dynamic {
  def selectDynamic(name: String) = name

  def applyDynamic(name: String)(args: Any*) =
    s"method '$name' called with arguments ${args.mkString("'", "', '", "'")}"

  def method(x: Int): String = {
    s"int method"
  }
}

object wdqw {
  def main(args: Array[String]): Unit = {
    val x = new DynTrait
    println(x.method(5))
    println(x.methods(2.4))
    println(x.method(5, "ss"))
  }
}
```
    Too many arguments for method method(int)
    
#### Rules from [SIP-17 - Type Dynamic](http://docs.scala-lang.org/sips/completed/type-dynamic.html)
. When typing an qual.sel, if type checking fails, provide one more rule before giving up:

If the static type of the receiver qual conforms to scala.Dynamic, and the selector sel is not one of the names applyDynamic, applyDynamicNamed, selectDynamic, or updateDynamic, then rewrite the selection to one of the following:

- If qual.sel is followed by a type argument list [Ts] (optionally) and an argument list (arg1, …, argn), where none of the arguments argi are named, and argn is not a sequence argument, i.e., of the shape e: _* (the argument list is required, though it may be empty), as in: qual.sel[Ts](arg1, …, argn), rewrite the expression to: qual.applyDynamic[Ts](“sel”)(arg1, …, argn)
- If qual.sel is followed by a type argument list [Ts] (optionally) and a non-empty named argument list (x1 = arg1, …, xn = argn) where some (but not all) name prefixes xi = might be missing (the argument list is required, and it must not end in a sequence argument), as in:
qual.sel[Ts](x1 = arg1, …, xn = argn), rewrite the expression to: qual.applyDynamicNamed[Ts](“sel”)(xs1 -> arg1, …, xsn -> argn), where each xsi is either the string “xi”, if the i’th argument carries the name xi or is the empty string “” if the i’th argument is unnamed.
- If qual.sel appears immediately on the left-hand side of an assignment, as in: qual.sel = expr, rewrite the expression to: qual.updateDynamic(“sel”)(expr)
- If qual.sel is not followed by arguments or an assignment operator: qual.selectDynamic[Ts](“sel”)


The result of the rewriting gets type checked in turn.

The selector may also be followed by some explicit type parameters. In that case, the type parameters are passed as given to the generated applyDynamic, applyDynamicNamed, and selectDynamic (but not updateDynamic) methods. For instance, 

 ```qual.meth[Int, String](x)```

would become

 ```qual.applyDynamic[Int, String](“meth”)(x)```

In summary: To support dynamic member dispatch a class or trait has to extend trait scala.Dynamic and has to implement some subset of the applyDynamic, applyDynamicNamed, selectDynamic, and updateDynamic methods. The precise type signature of these methods can be chosen freely by the implementer of the dynamic type.


### Ruby

Instead of supporting method overloading Ruby [overwrites existing methods](http://stackoverflow.com/questions/28976531/function-overloading-in-ruby)


### Go

[Why does Go not support overloading of methods and operators?](https://golang.org/doc/faq#overloading)

Method dispatch is simplified if it doesn't need to do type matching as well. Experience with other languages told us that having a variety of methods with the same name but different signatures was occasionally useful but that it could also be confusing and fragile in practice. Matching only by name and requiring consistency in the types was a major simplifying decision in Go's type system.

Regarding operator overloading, it seems more a convenience than an absolute requirement. Again, things are simpler without it.

### Js &&  Typescript

Функции вызываются только по именам. Добавление новой функции с тем же именем перезаписывает старую. Количество аргументов игнорируется.

```
<script>
function f(){
	 document.writeln("no arguments");
}
function f(s){
	 document.writeln("s argument");
}
f();
f(5);
f(5, 15);
</script>
```

### Rhino : [Calling Overloaded Methods](https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino/Scripting_Java)
[Java Method Overloading and LiveConnect 3](http://web.archive.org/web/20110623074154/http://www.mozilla.org/js/liveconnect/lc3_method_overloading.html#ConversionPreferences)


#### 3.1 Method Accessibility and Applicability

The first step in resolving a method invocation is to determine which methods of a class are accessible and applicable.  A Java method is accessible and applicable if all of the following are true:
The method is public.
If the invocation is a static invocation, the method must be a static method. If the invocation is an instance invocation, the method must not be static.
The number of parameters in the method declaration equals the number of argument expressions in the method invocation.
The type of each actual argument can be converted by LiveConnect method invocation conversion (See Section 3.3).
If there are no applicable methods for an invocation, an error occurs. If there is only one applicable method, it is the one invoked.
#### 3.2 Choose the Preferred Method

When choosing between two or more applicable methods, an algorithm is used that is similar in spirit to the ones used in Java and C++:
Suppose that U and S are both applicable methods for an invocation, each having n parameters.  Suppose, moreover, that the Java types of the parameters for method U are u1,...,un and the Java types of  the parameters for method S are s1,...,sn. Finally, the runtime JavaScript types of the actual arguments are t1,...,tn. Then the method U is preferred over method S iff
uj and sj are the same type, or
conversion to type uj is preferred to the conversion to type sj when converting from tj
for all j from 1 to n.
A method is said to be maximally preferred for a method invocation if it is applicable and there is no more preferred applicable method. If there is only one maximally preferred method, that method is necessarily preferred to all other applicable methods and it is the one invoked. If there is more than one maximally preferred method, an error occurs.

+ Prefered Table Java Method Overloading and LiveConnect 3

### [Rjb - Ruby Java Bridge](http://rjb.rubyforge.org/)

call overloaded method (with type informations)

Rjb inspect type of arguments, and then decides which method to be called. But it's not complete (suppose the case; if there are three methods like void foo(int), void foo(short), void foo(long), and the argument is 30), in this case you need to call with obj#_invoke as

```instance2 = instance._invoke('replaceAll', 'Ljava.lang.String;Ljava.lang.String;', 'hiki, 'rwiki')```

- ```obj#_invoke(name, sig, arg[, more args])``` invoke a method with name 'name' with type informations
- ```name```
the name of the method to be called
- ```sig```
type signature. You can find the type names at J2SE's Class#getName API documentation as arrays's encoded element type names.
