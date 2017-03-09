### Unit

```
class A {
    fun runner(): Unit {}
}
fun main(args: Array<String>) {
    val x: dynamic = A()
    val z =x.runner();
    println(z.toString())
}
```

```
kotlin.Unit
```
```
Exception in thread "main" java.lang.NullPointerException
```
