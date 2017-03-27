### К вопросам о дизайне

```
class A {
    fun doWork(x: Int, vararg y: Int) = x.toString() + y.fold("", { a, b -> a + b.toString() })
}

fun box(): String {
    val a: dynamic = A()
    val res = a.doWork(17, *IntArray(3, { _ -> 5 }))

    return if (res == "17555") "OK" else res
}
```


```
java.lang.IllegalStateException: TYPE_MISMATCH: Type mismatch: inferred type is IntArray 
but Array<out dynamic> was expected (12,29) in /varargKotlinUnpackArray.kt
```
