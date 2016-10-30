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