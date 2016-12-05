```
fun function1(x : String){
    println ("String")
}
fun function1(x : Int){
    println ("Int")
}
fun main(args: Array<String>) {
    val a : dynamic = 567;

    function1(a);
}
```


> Overload resolution ambiguity. All these functions match.

> public fun function1(x: Int): Unit defined in root package
  
> public fun function1(x: String): Unit defined in root package

```
@JvmStatic
fun getStringFieldFromDynamic(a: dynamic) {
    val res = a.b
}

@JvmStatic
fun getStringFieldFromClass(a: ClassWithStringField) {
    val res = a.b
}
```


|Benchmark|                                 Mode|  Cnt|           Score|           Error|  Units|
|---|---|---|---|---|---|
|JMHGetKotlinFieldBenchmark.dynamicField|  thrpt|   20|  2753335921,051 |± 257293382,186|  ops/s|
|JMHGetKotlinFieldBenchmark.kotlinField|   thrpt|   20|  2875538033,735 |± 157499609,497|  ops/s|
