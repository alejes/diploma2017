- Добавлено изменение target при изменении параметра функции
- Исправлен вызов dynamic на static
- Мигрировал на rc1.1. После миграции 300/2900 встроенных тестов падало.
- Починил падающие тесты
- Написал десяток тестов на динамик

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
