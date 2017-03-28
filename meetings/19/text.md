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
- Ещё вопрос
```
class A {
    fun doWork(x: Int) = 76
    fun doWork(x: Int?) = 79
}

fun box(): String {
    val a: dynamic = A()
    val res = a.doWork(17 as Int?)

    return if (res == 79) "OK" else res.toString()
}
```

- Во время исполнения мы не знаем какой тип был передан - Nullable или нет. И поэтому не можем выбрать нужную перегрузку. При компиляции мы можем передавать параметром маску нулёвости примитивных типов. Однако кажется что это не такой случай, что ради него требуется передавать эту маску и замедлять проверку перегрузок.



- Теперь у нас есть разрешение с varargами с Java и Kotlin
- Также я переписал рантайм, стало удобней и выделилось в отдельную компоненту поиск ссылки на метод/поле по его аргументам.
- Это понадобилось при вызове объектов. Как это происходит? нам надо поставить ссылку на что-то. Мы знаем что геттер объекта не изменится
