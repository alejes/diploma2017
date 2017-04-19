- obj.plusAssign(...) теперь возвращает Unit
- ListBuiltins

### Беда

```
fun box(): String {
    val z = listOf(1, 2, 3, 4, 5)
    var max = z[0]
    for (element in z) {
        max = maxOf(max, element)
    }
    return if (max == 5) "OK" else max.toString()
}
```


```
kotlin.DynamicBindException: class is not public: java.util.Arrays$ArrayList.get(int)Object/invokeVirtual, from ListIteratorKt
```
