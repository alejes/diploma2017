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
