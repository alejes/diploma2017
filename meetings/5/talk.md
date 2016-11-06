### Groovy difference between dynamic and static

[Jochen Theodorou](http://groovy.329449.n5.nabble.com/Overload-resolution-and-CompileStatic-tp5736491p5736496.html)

> my simpler version: 
```
def foo(Object x){1} 
def foo(String x){2} 
def create(){ return "" } 
 
@CompileStatic 
def bar() { 
   assert foo("") == 2 
   assert foo(new Object()) == 1 
   assert foo(create()) == 1 
} 

bar()
```
> The reasons simply being, that because create is declared with Objet 
(def), it is seen as something that will return Object and not String. 
Thus foo(Object) is used instead of foo(String). Yes we do type 
inference, but this is basically only for local variables. 

> bye Jochen 



### Clojure
#### [!] [My Strange question about null](https://groups.google.com/forum/#!topic/clojure/STwhI5t4Z4g)

[Interop with java](http://clojure-doc.org/articles/language/interop.html) + [Clojure Runtime](http://seb.xn--ho-hia.de/clojure-java-interoperability/)


#### Странные специфичные ситуации [gist](https://gist.github.com/Chouser/381625)
[Stack](http://stackoverflow.com/questions/2722856/how-do-i-call-overloaded-java-methods-in-clojure)
```
package foo;
public class TestInterop
{   public String test(int i)
    { return "Test(int)"; }

    public String test(Object i)
    { return "Test(Object)"; }
}
```
```
user=> (.test (new foo.TestInterop) 10)
"Test(Object)"
```

__Answer__

A discussion is going on in the #clojure channel on Freenode just now on this very topic. Chris Houser (who was going to post an answer, but ultimately decided he was too busy to do it) has posted [a Gist](http://gist.github.com/381625) which demonstrates what happens with a boolean vs. Object overloaded method; it turns out that in some scenarios, in addition to a (boolean ...) cast, a type hint is required. The discussion was quite enlightening, with a few dark corners of Clojure compilation process becoming nicely illuminated. (See links to IRC log below.)

Basically, if an object is created right in the method-calling form -- (.foo (Foo.) ...), say -- that type hint is not necessary; it is likewise not necessary if the object has been constructed as a value for a local in an enclosing let form (see update 2 below and my version of the Gist). If the object is obtained by Var lookup, though, a type hint is required -- which can be provided either on the Var itself or, at the call site, on the symbol used to refer to the Var.

[IRC log](http://gist.github.com/381625)

> Решение отсюда не смогло выбрать между int и Integer с перекосом в Integer


#### [Propose for function overloading hints](http://dev.clojure.org/display/design/Function+overloading)

#### Why existing solutions are not satisfactory
- Primitive-hinted functions are already available, but don't allow multiple overloads and do not help with different Object types
- You can use multi-methods or protocols, but these impose additional overhead, require extra code and provide open extension (which is unnecessary in many cases)
- You can manually write `instance?` checks, but these are less idiomatic and don't allow the compiler to optimise dispatch in cases where the type is already statically known
#### Proposed implementation
- The compiler should emit bytecode for a separate method for each function overload (possibly named `invokeStatic`, since the methods would be the same as those needed for static invocation / direct linking)
- In order to satisfy the `IFn` interface, the compiler should also generate a method using `java.lang.Object` parameters and return values. Internally, this could be implemented by generating a `cond` expression with `instance?` checks that invokes the correct typed variant (probably in order of declaration? or following Java dispatch conventions?)
- If an overloaded function signature is also allowable as a primitive-hinted function, it should also create the appropriate `invokePrim` method and implement the corresponding interface. In this sense, this implementation might be considered as a generalisation of the existing `invokePrim` functionality.