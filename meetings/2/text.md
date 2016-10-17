# Сравнение производительности Dynamic vs NotDynamic

|Compiler | [Time per iteration](http://blog.dhananjaynene.com/2008/07/performance-comparison-c-java-python-ruby-jython-jruby-groovy/) | [Fib(48)](https://gist.github.com/chanwit/133661) [Mail](http://www.mail-archive.com/mlvm-dev@openjdk.java.net/msg00821.html) |
|---|---| --- |
| Indy     |  23.903433212 microseconds | 98932.222809 ms |
| Standart |  22.292255485 microseconds | 248350.093248 ms |
| Static | ~ | 18076.178766 ms |

### Gradual typing
- Прочитал: http://wphomes.soic.indiana.edu/jsiek/what-is-gradual-typing/
- In progress: http://pleiad.dcc.uchile.cl/papers/2016/garciaAl-popl2016.pdf


### Type resolving
#### C#
[Rules](https://msdn.microsoft.com/en-us/library/aa691336(v=vs.71).aspx)
- Given the set of applicable candidate function members, the best function member in that set is located.
- If the set contains only one function member, then that function member is the best function member.
- Otherwise, the best function member is the one function member that is better than all other function members with respect to the given argument list, provided that each function member is compared to all other function members using the rules in [__Better function member rules__](https://msdn.microsoft.com/en-us/library/aa691338(v=vs.71).aspx]).
- If there is not exactly one function member that is better than all other function members, then the function member invocation is ambiguous and a compile-time error occurs.

__Better function member rules__:

Given an argument list A with a set of argument types {A1, A2, ..., AN} and two applicable function members MP and MQ with parameter types {P1, P2, ..., PN} and {Q1, Q2, ..., QN}, MP is defined to be a better function member than MQ if
- for each argument, the implicit conversion from AX to PX is not worse than the implicit conversion from AX to QX, and
- for at least one argument, the conversion from AX to PX is better than the conversion from AX to QX.
When performing this evaluation, if MP or MQ is applicable in its expanded form, then PX or QX refers to a parameter in the expanded form of the parameter list.
#### Groovy


[Documentation](http://groovy-lang.org/objectorientation.html#_method_selection_algorithm)
```
(TBD)
```

[Double Dispatch Distance Algorithm](https://issues.apache.org/jira/browse/GROOVY-5490)

[outline of method selection by Jochen "blackdrag" Theodorou](https://groups.google.com/forum/#!topic/jvm-languages/J-7GQf7sMLk)

```
1) build a list of all methods with the name of the method we want to calls
2) remove the methods that are not valid for the call
3) if more than one method remains calculate the "method distance" 
between the call and the method
4) the method with my minimum distance will be selected
5) if at the end I have two or more methods with the same minimum 
distance I have to report an error

this distance calculation consists of calculating a distance between 
parameter class and argument class (class distance) and then sum it up 
for all of them plus additional functionality for vargs. This class 
distance consists for normal classes of counting how many super classes 
of the argument class I have to go through until I meat the parameter 
class. the algorithm gets even more complicated through interfaces and 
primitive types.

An improvement would be to not to calculate this distance directly, but 
to calculate a distance to a generalized call and then cache it for the 
method... if it works well, then a simple subtraction (plus vargs) would 
give the distance and this would be pretty fast then.
```
### [Example of calculation](http://mail-archives.apache.org/mod_mbox/groovy-notifications/201507.mbox/%3CJIRA.12811725.1338725914000.174247.1436814004693@Atlassian.JIRA%3E)
```
static foo(Object o, Integer i) { "Integer won" }
static foo(String s, Object o) { "String won" }
assert foo("potato", new Integer(6)) =~ "Integer" 
```
distance(Class runtime, Class declaredParameter) ==> difference in level in inheritance
tree, e.g. distance(String, Object) == 1 - 0 == 1, etc.
therefore the total distance is just distance(param1) + distance(param2) + ...

In the sample code above, the runtime distance of foo #1 would be (distance(String, Object) + distance(Integer, Integer)) == 1 + 0 == 1
while distance of foo #2 would be (distance(String, String) + distance(Integer, Object)) == 0 + 2 == 2 (since Integer -> Number -> Object)

Therefore foo #1 should have been selected
```
 In Groovy a class can have different sets of methods at
the same time in different threads
```
```
The problem comes when I look at the method definition at runtime via
reflection. I can't tell the difference between def foo(a) {... and
def foo(Object a) {.... as they both have a single parameter of type
Object.
```

## [Dynalang](http://dynalang.sourceforge.net/)

[Implementation](https://sourceforge.net/p/dynalang/code/HEAD/tree/trunk/invoke/src/org/dynalang/dynalink/beans/OverloadedDynamicMethod.java#l26)


## [ Method Invocation Rules in java](https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.12)