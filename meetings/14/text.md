- Добавлено изменение target при изменении параметра функции
- Исправлен вызов dynamic на static
- Мигрировал на rc1.1. После миграции 300/2900 встроенных тестов падало.
- Починил падающие тесты
- Написал десяток тестов на динамик
### Вопросы
```
class K {
    fun reverse(s: String): String {
        return s.reversed()
    }

    companion object {
        fun getRef() = K::reverse
    }
}
```
- Кажется что мы не можем здесь иметь динамического кандидата

### Не проходят два теста
```
interface  B<T> {
    val bar: T
}

fun String.foo() = object : B<String> {
    override val bar: String = length.toString()
}

class C {

    fun String.extension() = this.length	 	

    fun String.fooInClass() = object : B<String> {
        override val bar: String = extension().toString()
    }

    fun fooInClass(s: String) =  s.fooInClass().bar
}

fun box(): String {
    return "OK"
}
```
error: type checking has run into a recursive problem. Easiest workaround: specify types of your declarations explicitly
    fun fooInClass(s: String) =  s.fooInClass().bar
```
//KT-3190 Compiler crash if function called 'invoke' calls a closure
// IGNORE_BACKEND: JS
// JS backend does not allow to implement Function{N} interfaces

fun box(): String {
    val test = Cached<Int,Int>({ it + 2 })
    return if (test(1) == 3) "OK" else "fail"
}

class Cached<K, V>(private val generate: (K)->V): Function1<K, V> {
    val store = HashMap<K, V>()

    // Everything works just fine if 'invoke' method is renamed to, for example, 'get'
    override fun invoke(p1: K) = store.getOrPut(p1) { generate(p1) }
}

//from library
fun <K,V> MutableMap<K,V>.getOrPut(key: K, defaultValue: ()-> V) : V {
    if (this.containsKey(key)) {
        return this.get(key) as V
    } else {
        val answer = defaultValue()
        this.put(key, answer)
        return answer
    }
}
```
kt3190
