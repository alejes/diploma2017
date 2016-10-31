### Groovy
[int vs Integer call](http://stackoverflow.com/questions/39411057/difference-between-int-and-integer-type-in-groovy)


### C#

#### Array Dynamic
```
Array<T> singletonArray<T>(T t) {
    Array<T> array = new Array<T>(1);
    array[0] = t;return array;
}
dynamic str = "Hello";
Array<String> strs = singletonArray(str);`
```
So one might as well replace _dynamic_ with _Object_ at run time,which is what C# chooses to do. However, C# does this excessively.One cannot assign a _Array<dynamic>_ to a _Array<String>_ ( at compile time) in C#. Unless one is getting values from the array,the typing rules for an _Array<dynamic>_ are exactly the same as for _Array<Object>_. This makes C#’s dynamic type rather weak,and it is the reason way C# has to give singletonArray(str) the completely uninformative type _dynamic_.

__Suggestion of Ross Tate__

This perspective makes most typing decisions obvious. Consider
whether an Array<dynamic> should be assignable to an
Array<String>. Wherever dynamic occurs in the types in question,
one can treat it optimistically and pretend it has any type they
hope it has. In this case, we could optimistically pretend/hope the
dynamic in Array<dynamic> is String, which makes the assignment
type check. This coincides with the reasoning in many works
on gradual typing, and Ina and Igarashi even consider this exact
example (albeit with different class names) [10].
Now reconsider the assignment of singletonArray(str) to
the variable Array<String> strs. This does 2 things:
- At run time, a check that the value is a subtype of Array<String>
is inserted. Since it is already known to be an Array of something,
and Array is final, this amounts to a quick check that
that something is equivalent to String.
- At compile time, it changes the typing mode of the expression
from semi-optimistic to completely pessimistic.

The second step is important, since although subtypes can be
converted implicitly, typing modes must be converted explicitly.
This means that modal subtyping does not need to be transitive,
which is a known interesting property of gradual type systems [17].



### Jython
[Robert W. Bill: Jython for Java Programmers, p.145](https://books.google.ru/books?id=-7MMHfZ8bc8C&printsec=frontcover&hl=ru#v=onepage&q&f=false)
![](https://github.com/alejes/diploma2017/blob/master/meetings/4/sb+jython.JPG?raw=true)

#### Jython under the hood

[PyClass](https://sourceforge.net/p/jython/svn/HEAD/tree/trunk/jython/src/org/python/core/PyClass.java#l88)
```
 PyObject lookup(String name) {
        PyObject result = __dict__.__finditem__(name);
        if (result == null && __bases__ != null) {
            for (PyObject base : __bases__.getArray()) {
                result = ((PyClass)base).lookup(name);
                if (result != null) {
                    break;
                }
            }
        }
        return result;
    }
```

[PyJavaType](https://sourceforge.net/p/jython/svn/HEAD/tree/trunk/jython/src/org/python/core/PyJavaType.java#l749)
```
/**
     * Private, protected or package protected classes that implement public interfaces or extend
     * public classes can't have their implementations of the methods of their supertypes called
     * through reflection due to Sun VM bug 4071957(http://tinyurl.com/le9vo). They can be called
     * through the supertype version of the method though. Unfortunately we can't just let normal
     * mro lookup of those methods handle routing the call to the correct version as a class can
     * implement interfaces or classes that each have methods with the same name that takes
     * different number or types of arguments. Instead this method goes through all interfaces
     * implemented by this class, and combines same-named methods into a single PyReflectedFunction.
     *
     * Prior to Jython 2.5, this was handled in PyJavaClass.setMethods by setting methods in package
     * protected classes accessible which made them callable through reflection. That had the
     * drawback of failing when running in a security environment that didn't allow setting
     * accessibility, so this method replaced it.
     */
    private void handleSuperMethodArgCollisions(Class<?> forClass) {
        for (Class<?> iface : forClass.getInterfaces()) {
            mergeMethods(iface);
        }
        if (forClass.getSuperclass() != null) {
            mergeMethods(forClass.getSuperclass());
            if (!Modifier.isPublic(forClass.getSuperclass().getModifiers())) {
                // If the superclass is also not public, it needs to get the same treatment as we
                // can't call its methods either.
                handleSuperMethodArgCollisions(forClass.getSuperclass());
            }
        }
    }

    private void mergeMethods(Class<?> parent) {
        for (Method meth : parent.getMethods()) {
            if (!Modifier.isPublic(meth.getDeclaringClass().getModifiers())) {
                // Ignore methods from non-public interfaces as they're similarly bugged
                continue;
            }
            String nmethname = normalize(meth.getName());
            PyObject[] where = new PyObject[1];
            PyObject obj = lookup_where_mro(nmethname, where);
            if (obj == null) {
                // Nothing in our supertype hierarchy defines something with this name, so it
                // must not be visible there.
                continue;
            } else if (where[0] == this) {
                // This method is the only thing defining items in this class' dict, so it must
                // be a PyReflectedFunction created here. See if it needs the current method
                // added to it.
                if (!((PyReflectedFunction)obj).handles(meth)) {
                    ((PyReflectedFunction)obj).addMethod(meth);
                }
            } else {
                // There's something in a superclass with the same name. Add an item to this type's
                // dict to hide it.  If it's this method, nothing's changed.  If it's a field, we
                // want to make the method visible.  If it's a different method, it'll be added to
                // the reflected function created here in a later call.
                dict.__setitem__(nmethname, new PyReflectedFunction(meth));
            }
        }
    }
```

[PyReflectedFunction](https://sourceforge.net/p/jython/svn/HEAD/tree/trunk/jython/src/org/python/core/PyReflectedFunction.java#l157)
```
 public PyObject __call__(PyObject self, PyObject[] args, String[] keywords) {
        ReflectedCallData callData = new ReflectedCallData();
        ReflectedArgs match = null;
        for (int i = 0; i < nargs && match == null; i++) {
            // System.err.println(rargs.toString());
            if (argslist[i].matches(self, args, keywords, callData)) {
                match = argslist[i];
            }
        }
        if (match == null) {
            throwError(callData.errArg, args.length, self != null, keywords.length != 0);
        }
        Object cself = callData.self;
        Method m = (Method)match.data;

        // If this is a direct call to a Java class instance method with a PyProxy instance as the
        // arg, use the super__ version to actually route this through the method on the class.
        if (self == null && cself != null && cself instanceof PyProxy
                && !__name__.startsWith("super__")
                && match.declaringClass != cself.getClass()) {
            String mname = ("super__" + __name__);
            try {
                m = cself.getClass().getMethod(mname, m.getParameterTypes());
            } catch (Exception e) {
                throw Py.JavaError(e);
            }
        }
        Object o;
        try {
            o = m.invoke(cself, callData.getArgsArray());
        } catch (Throwable t) {
            throw Py.JavaError(t);
        }
        return Py.java2py(o);
    }
```

[ReflectedArgs.matches](https://sourceforge.net/p/jython/svn/HEAD/tree/trunk/jython/src/org/python/core/ReflectedArgs.java#l44)


### [Jsmall](http://sci-hub.cc/10.1016/j.cl.2011.03.002)

3.2. Evaluation of JSmall’s dynamic type tag

```
// Javacode
public classXMLWriter {
    public XMLWriter() { y}
    public void write(inte) { y}
    public void write(Integere) { y}
    public void write(XMLElemente) { y}
    public void write(StructuredXMLElemente) { y}
    public void write(Serializablee) { y}
}
```

```
‘‘JSmall code’’
w :¼‘XMLWriter’ asJavaClassnew.
w write:10. ‘‘Exception raised’’
w write:(10type:‘int’). ‘‘call write(int)’’
w write:(10type:‘Integer’). ‘‘call write(Integer)’’
```

The previous section essentially focuses on passing annotated objects from JSmall to Java.The same mechanism applies 
in the other way around.__Values returned__ to JSmall __from Java are automatically annotated with the return type called Java
 method__. The aimofthis automatic annotation is not to lose the type when results from calling Java methods have to be
 used as arguments when calling another Java method.
Note that the return type declared in the Java method isused to __tag the returned value__, and not the dynamic type of 
the value.

###  [Java Lite](http://sci-hub.cc/10.1016/j.cl.2011.03.002)

![JavaLite](https://github.com/alejes/diploma2017/blob/master/meetings/4/javaLite.JPG?raw=true)


### F#
[Dynamic operator ?](https://msdn.microsoft.com/en-us/library/hh304373(v=vs.100).aspx)

How the Dynamic Operator Works
The dynamic operator in F# is extremely simple but surprisingly powerful. Assume that the MyType type implements the dynamic operator as a static method and that myVal is a value of MyType. An expression that uses the dynamic operator such as myVal?Foo is translated to a call that passes the name Foo to the dynamic operator (that is, it is translated to MyType.(?) myVal "Foo").. This way, the implementation can perform a dynamic lookup using the string.

The operator doesn't have a direct support for method calls. However, it can return a function that takes an arbitrary argument. If the return type of (?) is a function of type 'T -> unit, then it is possible to write, for example: myVal?Foo(1, "test"). This is translated to a call that invokes the dynamic operator (which returns a function) and then calls the returned function with a tuple as an argument.

The helper for calling stored procedures uses exactly this approach. It returns a function that expects a tuple. When called, the function uses F# reflection to extract a list of arguments from the tuple (e.g., the number 1 and the string "test" in the previous example) and passes these arguments to the SQL-stored procedure. The following section shows a utility function that takes a tuple as input and constructs a SQL command object.

#### [Implementation](http://stackoverflow.com/questions/5057672/looking-for-robust-general-op-dynamic-implementation)
There is a module FSharp.Interop.Dynamic, on nuget that should robustly handle the dynamic operator using the dlr.

It has several advantages over a lot of the snippets out there.

Performance it uses [Dynamitey](https://github.com/ekonbenefits/dynamitey/wiki/UsageReallyLateBinding) for the dlr call which implements caching and is a PCL Library
Handles methods that return void, you'll get a binding exception if you don't discard results of those.
The dlr handles the case of calling a delegate return by a function automatically, this will also allow you to do the same with an FSharpFunc
Adds an !? prefix operator to handle invoking directly dynamic objects and functions you don't have the type at runtime.

It's open source, Apache license, you can look at the [implementation](https://github.com/fsprojects/FSharp.Interop.Dynamic/blob/master/FSharp.Interop.Dynamic/Dynamic.fs) and it includes [unit test example](https://github.com/fsprojects/FSharp.Interop.Dynamic/blob/master/Tests/Library1.fs) cases.

### IronPython
#### [Method overloads](http://ironpython.net/documentation/dotnet/dotnet.html#method-overloads)

.NET supports overloading methods by both number of arguments and type of arguments. When IronPython code calls an overloaded method, __IronPython tries to select one of the overloads at runtime based on the number and type of arguments passed to the method, and also names of any keyword arguments__. In most cases, the expected overload gets selected. Selecting an overload is easy when the argument types are an exact match with one of the overload signatures:
```
>>> from System.Collections import BitArray
>>> ba = BitArray(5) # calls __new__(System.Int32)
>>> ba = BitArray(5, True) # calls __new__(System.Int32, System.Boolean)
>>> ba = BitArray(ba) # calls __new__(System.Collections.BitArray)
```
The argument types do not have be an exact match with the method signature. IronPython will try to convert the arguments if an unamibguous conversion exists to one of the overload signatures. The following code calls ```__new__(System.Int32)``` even though there are two constructors which take one argument, and neither of them accept a float as an argument:
```
>>> ba = BitArray(5.0)
```
However, note that IronPython will raise a TypeError if there are conversions to more than one of the overloads:
```
>>> BitArray((1, 2, 3))
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
TypeError: Multiple targets could match: BitArray(Array[Byte]), BitArray(Array[bool]), BitArray(Array[int])
```
If you want to control the exact overload that gets called, you can use the Overloads method on method objects:
```
>>> int_bool_new = BitArray.__new__.Overloads[int, type(True)]
>>> ba = int_bool_new(BitArray, 5, True) # calls __new__(System.Int32, System.Boolean)
>>> ba = int_bool_new(BitArray, 5, "hello") # converts "hello" to a System.Boolan
>>> ba = int_bool_new(BitArray, 5)
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
TypeError: __new__() takes exactly 2 arguments (1 given)
```

[Bag with out parameters](http://darenatwork.blogspot.ru/2015/10/accessing-specific-overloads-in.html)

revitpythonshell provides two very similar methods to load a family.
```
LoadFamily(self: Document, filename:str) -> (bool, Family)
LoadFamily(self: Document, filename:str) -> bool
```
So it seems like only the return values are different. I have tried to calling it in several different ways:
```
(success, newFamily) = doc.LoadFamily(path) 
success, newFamily = doc.LoadFamily(path) 
o = doc.LoadFamily(path) 
```
But I always just get a bool back. I want the Family too.
What is happening here is that the c# definitions of the method are:
```
public bool LoadFamily(
    string filename
)
```
and
```
public bool LoadFamily(
    string filename,
    out Family family
)
```
The IronPython syntax candy for out parameters, returning a tuple of results, can’t automatically be selected here, because calling LoadFamily with just a string argument matches the first method overload. 


### Nashorn

[Parameter overloading](http://winterbe.com/posts/2014/04/05/java8-nashorn-tutorial/)

Methods and functions can either be called with the point notation or with the square braces notation.
```
var System = Java.type('java.lang.System');
System.out.println(10);              // 10
System.out["println"](11.0);         // 11.0
System.out["println(double)"](12);   // 12.0
```
Passing the optional parameter type println(double) when calling a method with overloaded parameters determines the exact method to be called.

#### [Varargs and implicit conversion](http://stackoverflow.com/questions/25603191/nashorn-bug-when-calling-overloaded-method-with-varargs-parameter/25610856#25610856)

As the guy who wrote the overload resolution mechanism for Nashorn, I'm always fascinated with corner cases that people run into. For better or worse, here's how this ends up being invoked:

Nashorn's overload method resolution mimics Java Language Specification (JLS) as much as possible, but allows for JavaScript-specific conversions too. JLS says that when selecting a method to invoke for an overloaded name, variable arity methods can be considered for invocation only when there is no applicable fixed arity method. Normally, when invoking from Java ```test(String)``` would not be an applicable to an invocation with an int, so the ```test(Integer...)``` method would get invoked. However, since JavaScript actually allows number-to-string implicit conversion, it is applicable, and considered before any variable arity methods. Hence the observed behavior. Arity trumps non-conversion. If you added a test(int) method, it'd be invoked before the String method, as it's fixed arity and more specific than the String one.

You could argue that we should alter the algorithm for choosing the method. A lot of thought has been given to this since even before the Nashorn project (even back when I was developing Dynalink independently). Current code (as embodied in the Dynalink library, which Nashorn actually builds upon) follows JLS to the letter and in absence of language-specific type conversions will choose the same methods as Java would. However, as soon as you start relaxing your type system, things start to subtly change, and the more you relax it, the more they'll change (and JavaScript relaxes a lot), and any change to the choice algorithm will have some other weird behavior that someone else will run into… it just comes with the relaxed type system, I'm afraid. For example:

- If we allowed varargs to be considered together with fixargs, we'd need to invent a "more specific than" relation among differing arity methods, something that doesn't exist in JLS and thus isn't compatible with it, and would cause varargs to sometimes be invoked when otherwise JLS would prescribe fixargs invocation.
- If we disallowed JS-allowed conversions (thus forcing ```test(String)``` to not be considered applicable to an ```int``` parameter), some JS developers would feel encumbered by needing to contort their program into invoking the String method (e.g. doing ```test(String(x))``` to ensure ```x``` is a string, etc.
>As you can see, no matter what we do, something else would suffer; overloaded method selection is in a tight spot between Java and JS type systems and very sensitive to even small changes in the logic.

Finally, when you manually select among overloads, you can also stick to unqualified type names, as long as there's no ambiguity in potential methods signatures for the package name in the argument position, that is

```
API["test(Integer[])"](1);
```
should work too, no need for the java.lang. prefix. That might ease the syntactic noise a bit, unless you can rework the API.

HTH, Attila.


[read this](https://blog.jooq.org/2014/09/19/learn-how-nashorn-prevents-effective-api-evolution-on-a-new-level/) in progress..
