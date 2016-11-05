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