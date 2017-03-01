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
Если у нас есть перегрузка ```String``` и ```dynamic```.
```
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
67890
```
А если мы туда передадим не String? Хотелось бы чтобы появился dynamic.
```
fun function1(x: String): Int {
    return 67890
}
fun function1(x: dynamic): Int {
    return 83
}


fun main(args: Array<String>) {
    //val x: dynamic = "ww"
    val x: dynamic = 22121
    val z = function1(x)
    println(z)
}
```
```
67890
```
Ничего не поменялось.

Так, а теперь добавим новых перегрузок
```
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

- Отсутствующие методы
```
fun kotlin.Int.plus(kotlin.Int): kotlin.Int
Exception in thread "main" kotlin.reflect.jvm.internal.KotlinReflectionInternalError: Call is not yet supported for this function: public final operator fun plus(other: kotlin.Int): kotlin.Int defined in kotlin.Int[DeserializedSimpleFunctionDescriptor@5f683daf] (member = null)
	at kotlin.reflect.jvm.internal.KFunctionImpl$caller$2.invoke(KFunctionImpl.kt:96)
	at kotlin.reflect.jvm.internal.KFunctionImpl$caller$2.invoke(KFunctionImpl.kt:36)
	at kotlin.reflect.jvm.internal.ReflectProperties$LazySoftVal.invoke(ReflectProperties.java:93)
	at kotlin.reflect.jvm.internal.ReflectProperties$Val.getValue(ReflectProperties.java:32)
	at kotlin.reflect.jvm.internal.KFunctionImpl.getCaller(KFunctionImpl.kt)
	at kotlin.reflect.jvm.internal.KCallableImpl.call(KCallableImpl.kt:107)
```



```
class A {
    fun work(x: Int){
        println("All works:" + x)
    }
}
fun main(args: Array<String>) {
	val x: dynamic = A();
    x.work(15);
}
```
```
Unhandled JavaScript exception
```
