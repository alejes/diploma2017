- К вопросу о перегрузках в JS
- Исходные данные: http://try.kotlinlang.org . JavaScipt, v1.1RC
```
fun function1(x: Int): Int {
    return 5
}

fun function1(x: Double): Int {
    return 67890
}

fun main(args: Array<String>) {
    val x: dynamic = 23L
    val z = function1(x)
    println(z)
}
```
```
Error:(11, 12) Overload resolution ambiguity: 
public fun function1(x: Double): Int defined in root package
public fun function1(x: Int): Int defined in root package
```

А если
```
fun function1(x: Int): Int {
    return 5
}

fun function1(x: Long): Int {
    return 67890
}

fun main(args: Array<String>) {
    val x: dynamic = 23L
    val z = function1(x)
    println(z)
}
```
```
5
```

```
Так, а теперь добавим новых перегрузок
fun function1(x: Int): Int {
    return 5
}

fun function1(x: String): Int {
    return 67890
}

fun function1(x: dynamic): Int {
    return 83
}


fun main(args: Array<String>) {
    val x: dynamic = "ww"
    val z = function1(x)
    println(z)
}
```

```
Error:(16, 12) Overload resolution ambiguity: 
public fun function1(x: dynamic): Int defined in root package
public fun function1(x: Int): Int defined in root package
public fun function1(x: String): Int defined in root package
```
