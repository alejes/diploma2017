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

- Именнованные аргументы
    - Вопросы:
        - Нужны ли вообще? Только из-за них появляется зависимость на kotlin-reflect для узнавания имени аргументов.
        - Разрешать ли именнованные аргументы в java вызовах? Ибо они без ухищрений не появятся - argsN [Уже написано. Но это будет влиять на производительность. Также непонятно как отличить генерированные от реальных при коллизии имён. Предлагаю что - нет]
        
        
        
        
- Замеры
- Фибоначчи

|Benchmark|(n)|Mode|Cnt|Score|Error|Units|
|---|---|---|---|---|---|---|
|groovyFibLongStatic|10|avgt|60|0.169|±   0.001|us/op|
|kotlinFibLong|10|avgt|60|0.169|±   0.001|us/op|
|kotlinFibDynamic|10|avgt|60|0.391|±   0.004|us/op|
|groovyFibInvokeDynamic|10|avgt|60|0.97|±   0.002|us/op|
|groovyFibTraditional|10|avgt|60|2.059|±   0.002|us/op|
|kotlinFibLong|20|avgt|60|20.405|±   0.092|us/op|
|groovyFibLongStatic|20|avgt|60|21.384|±   0.007|us/op|
|kotlinFibDynamic|20|avgt|60|62.455|±   0.106|us/op|
|groovyFibInvokeDynamic|20|avgt|60|118.05|±   0.338|us/op|
|groovyFibTraditional|20|avgt|60|327.21|±   1.698|us/op|
|kotlinFibLong|30|avgt|60|2490.306|±   1.197|us/op|
|groovyFibLongStatic|30|avgt|60|2631.639|±   4.023|us/op|
|kotlinFibDynamic|30|avgt|60|7710.093|±  30.019|us/op|
|groovyFibInvokeDynamic|30|avgt|60|14514.545|±  22.610|us/op|
|groovyFibTraditional|30|avgt|60|39316.698|± 565.434|us/op|


- Квадрат матрицы

|Benchmark|(n)|Mode|Cnt|Score|Error|Units|
|kotlinInt|10|avgt|60|11.349|±  0.064|us/op|
|kotlinDynamic|10|avgt|60|21.204|±  0.096|us/op|
|groovyIntStatic|10|avgt|60|21.76|±  1.729|us/op|
|groovyInvokeDynamic|10|avgt|60|75.645|±  0.540|us/op|
|groovyTraditional|10|avgt|60|79.272|±  0.762|us/op|
|kotlinInt|20|avgt|60|87.447|±  0.380|us/op|
|kotlinDynamic|20|avgt|60|159.003|±  1.177|us/op|
|groovyIntStatic|20|avgt|60|181.813|± 12.143|us/op|
|kotlinInt|30|avgt|60|283.335|±  0.614|us/op|
|groovyIntStatic|30|avgt|60|451.818|±  1.412|us/op|
|groovyInvokeDynamic|20|avgt|60|569.262|±  1.644|us/op|
|kotlinDynamic|30|avgt|60|577.256|±  4.686|us/op|
|groovyTraditional|20|avgt|60|605.065|±  5.233|us/op|
|groovyInvokeDynamic|30|avgt|60|1913.036|±  4.767|us/op|
|groovyTraditional|30|avgt|60|2496.581|± 34.135|us/op|
