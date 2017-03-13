### Unit

```
class A {
    fun runner(): Unit {}
}
fun main(args: Array<String>) {
    val x: dynamic = A()
    val z = x.runner()
    println(z.toString())
}
```
Обычная ситуация:
```
kotlin.Unit
```
dynamic:
```
Exception in thread "main" java.lang.NullPointerException
```
- Стоит ли заморачиваться с передачей именнованных параметров?

- Аргументы по умолчанию:
```
class A {
    fun function1(x: Int, y: Int = 54, z: Int = 59): Int {
        return x + y + z
    }
}
fun main(args: Array<String>) {
    val a: dynamic = A()
    println(a.function1(10).toString())
}
```
```
123
```
