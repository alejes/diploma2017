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