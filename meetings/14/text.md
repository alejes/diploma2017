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
