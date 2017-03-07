### Немного из бибилиотеки JS

```
public fun print(message: kotlin.Any?): kotlin.Unit { /* compiled code */ }
public fun println(): kotlin.Unit { /* compiled code */ 
public fun println(message: kotlin.Any?): kotlin.Unit { /* compiled code */ }
```
```
fun main(args: Array<String>) {
    val z: dynamic = 5;
    println(z)
}
```

```
error: overload resolution ambiguity: 
@InlineOnly public inline fun println(message: Any?): Unit defined in kotlin.io
@InlineOnly public inline fun println(message: Boolean): Unit defined in kotlin.io
@InlineOnly public inline fun println(message: Byte): Unit defined in kotlin.io
@InlineOnly public inline fun println(message: Char): Unit defined in kotlin.io
@InlineOnly public inline fun println(message: CharArray): Unit defined in kotlin.io
@InlineOnly public inline fun println(message: Double): Unit defined in kotlin.io
@InlineOnly public inline fun println(message: Float): Unit defined in kotlin.io
@InlineOnly public inline fun println(message: Int): Unit defined in kotlin.io
@InlineOnly public inline fun println(message: Long): Unit defined in kotlin.io
@InlineOnly public inline fun println(message: Short): Unit defined in kotlin.io
```


### Сейчас ломается
```
private fun getChainOrNull(): dynamic {
    val chain: dynamic = 5
    return chain.takeIf { it != 5 }
}

/*
private fun InternalHashCodeMap::getChainOrNull(hashCode: Int): Array<MutableEntry<K, V>>? {
    val chain = backingMap[hashCode].unsafeCast<Array<MutableEntry<K, V>>?>()
    return chain.takeIf { it !== undefined }
}*/

fun main(args: Array<String>) {

}
```

### Fork

#### Run complete. Total time: 00:30:12

|Benchmark               |(n) | Mode | Cnt |     Score|     Error | Units |
|---|---|---|---|---|---|---|
|groovyIntDynamic        | 10 | avgt |  60 |     1.832| ±   0.020 | us/op |
|groovyIntDynamic        | 20 | avgt |  60 |   229.138| ±   0.923 | us/op |
|groovyIntDynamic        | 30 | avgt |  60 | 27971.273| ± 118.367 | us/op |
|groovyIntInvokeDynamic  | 10 | avgt |  60 |     0.432| ±   0.001 | us/op |
|groovyIntInvokeDynamic  | 20 | avgt |  60 |    59.054| ±   0.150 | us/op |
|groovyIntInvokeDynamic  | 30 | avgt |  60 |  6693.889| ±  77.381 | us/op |
|groovyIntStaticDynamic  | 10 | avgt |  60 |     0.173| ±   0.001 | us/op |
|groovyIntStaticDynamic  | 20 | avgt |  60 |    21.394| ±   0.280 | us/op |
|groovyIntStaticDynamic  | 30 | avgt |  60 |  2629.358| ±  33.723 | us/op |
|kotlinDynamic           | 10 | avgt |  60 |     0.394| ±   0.001 | us/op |
|kotlinDynamic           | 20 | avgt |  60 |    59.363| ±   0.147 | us/op |
|kotlinDynamic           | 30 | avgt |  60 |  7397.113| ±  54.862 | us/op |
|kotlinInt               | 10 | avgt |  60 |     0.212| ±   0.001 | us/op |
|kotlinInt               | 20 | avgt |  60 |    26.265| ±   0.020 | us/op |
|kotlinInt               | 30 | avgt |   60|  3087.734| ±  93.085 | us/op |


### Перемножение матриц
```
class KotlinRunner {
    companion object {
        @JvmStatic
        fun matrixSquare(source: dynamic): dynamic {
            val destination: dynamic = mutableListOf<MutableList<Int>>();
            for (i in 0..source.size-1) {
                destination.add(mutableListOf<Int>())
                for (j in 0..source.size-1) {
                    destination[i].add(j, 0)
                }
            }


            for (i in 0..source.size-1) {
                for (j in 0..source.size-1) {
                    for (k in 0..source.size-1) {
			val temp = source[i][k] * source[k][j];
                        destination[i][j] = temp + destination[i][j];
                    }
                }
            }

            return destination;
        }

        @JvmStatic
        fun matrixSquareProxy(source: List<List<Int>>): List<List<Int>> {
            //return MutableList<List<Int>>(5, { _ -> MutableList(5, { _ -> 5 }) });
            return matrixSquare(source)
        }
    }
}
```
__target__: notebook

|Benchmark            |(n) | Mode|  Cnt|     Score |   Error | Units |
|---|---|---|---|---|---|---|
|groovyIntStatic      | 10 | avgt | 200 |   33,076 |±  1,074 | us/op |
|groovyIntStatic      | 20 | avgt | 200 |  171,737 |±  2,081 | us/op |
|groovyIntStatic      | 30 | avgt | 200 |  563,543 |±  6,341 | us/op |
|groovyInvokeDynamic  | 10 | avgt | 200 |   93,196 |±  1,304 | us/op |
|groovyInvokeDynamic  | 20 | avgt | 200 |  691,998 |±  8,044 | us/op |
|groovyInvokeDynamic  | 30 | avgt | 200 | 2304,544 |± 27,708 | us/op |
|groovyTraditional    | 10 | avgt | 200 |  103,212 |±  0,803 | us/op |
|groovyTraditional    | 20 | avgt | 200 |  775,888 |±  1,975 | us/op |
|groovyTraditional    | 30 | avgt | 200 | 2943,794 |± 20,273 | us/op |
|kotlinDynamic        | 10 | avgt | 200 |   25,433 |±  0,174 | us/op |
|kotlinDynamic        | 20 | avgt | 200 |  190,027 |±  2,143 | us/op |
|kotlinDynamic        | 30 | avgt | 200 |  644,265 |±  6,764 | us/op |
|kotlinInt            |  10|  avgt|   60|   18,094 |±  0,335 | us/op |
|kotlinInt            |  20|  avgt|   60|  138,086 |±  3,387 | us/op |
|kotlinInt            |  30|  avgt|   60|  461,519 |± 10,596 | us/op |


### InlineOnly
```
fun main(args: Array<String>) {
    val n = 5
    val z = MutableList(n   , {_->46})
}
```

```
Exception in thread "main" kotlin.DynamicBindException: Runtime: cannot find target method MutableList
	at kotlin.DynamicSelector$MethodSelector.genMethodClass(DynamicSelector.java:291)
	at kotlin.DynamicSelector$MethodSelector.setCallSite(DynamicSelector.java:155)
	at kotlin.DynamicMetaFactory.invokeProxy(DynamicMetaFactory.java:94)
	at MainKt.main(main.kt:286)
```
via kotlin-reflection
```
Exception in thread "main" java.lang.UnsupportedOperationException: 
Packages and file facades are not yet supported in Kotlin reflection. 
Meanwhile please use Java reflection to inspect 
this class: class kotlin.collections.CollectionsKt
```


### TODO
- += IntrinsicCallable.genDynamicInstruction
- Exception in thread "main" kotlin.DynamicBindException: Runtime: cannot find target method plusAssign

```
fun main(args: Array<String>) {
    var x: dynamic = 5;
    x += 5;
    println(x);
}
```
- exception: org.jetbrains.kotlin.codegen.CompilationException: Back-end (JVM) Internal error: no setter specified Cause: no setter specified
	- org.jetbrains.kotlin.codegen.StackValue$CollectionElement.storeSelector(StackValue.java:1090)
```
fun main(args: Array<String>) {
    var x: dynamic = 5;
    val a: dynamic = mutableListOf<Int>();
    a[0] += x;
    println(a[0]);
}
```
