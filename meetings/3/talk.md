Intro
### C#

[Own DynamicObject Like Scala](https://msdn.microsoft.com/en-us/library/system.dynamic.dynamicobject(v=vs.110).aspx)

```
7.2.3 Types of constituent expressions

When an operation is statically bound, the type of a constituent expression (e.g. a receiver, and argument, an index or an operand) is always considered to be the compile-time type of that expression. When an operation is dynamically bound, the type of a constituent expression is determined in different ways depending on the compile-time type of the constituent expression:

• A constituent expression of compile-time type dynamic is considered to have the type of the actual value that the expression evaluates to at runtime

• A constituent expression whose compile-time type is a type parameter is considered to have the type which the type parameter is bound to at runtime

• Otherwise the constituent expression is considered to have its compile-time type.
```

[Documentation](http://dlr.codeplex.com/wikipage?title=Docs%20and%20specs&referringTitle=Documentation) [ [Short Intro](https://msdn.microsoft.com/en-us/library/dd233052(v=vs.110).aspx) ]

[Difference between dynamic and compile resolution](http://codinglight.blogspot.ru/2009/05/dynamic-type-and-runtime-overload.html)