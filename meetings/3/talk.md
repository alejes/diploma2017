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



```
7.2.3 Types of constituent expressions

When an operation is statically bound, the type of a constituent expression (e.g. a receiver, and argument, an index or an operand) is always considered to be the compile-time type of that expression. When an operation is dynamically bound, the type of a constituent expression is determined in different ways depending on the compile-time type of the constituent expression:

• A constituent expression of compile-time type dynamic is considered to have the type of the actual value that the expression evaluates to at runtime

• A constituent expression whose compile-time type is a type parameter is considered to have the type which the type parameter is bound to at runtime

• Otherwise the constituent expression is considered to have its compile-time type.
```

[Documentation](http://dlr.codeplex.com/wikipage?title=Docs%20and%20specs&referringTitle=Documentation) [ [Short Intro](https://msdn.microsoft.com/en-us/library/dd233052(v=vs.110).aspx) ]

[Difference between dynamic and compile resolution](http://codinglight.blogspot.ru/2009/05/dynamic-type-and-runtime-overload.html)