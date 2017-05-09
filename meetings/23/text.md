```
open class A
class B : A()

class Call {
    operator fun invoke(x: B) = "B"
}

class MyClass {
    val field = Call()
    fun field(x: A) = "A"
}

fun box(): String {
    val a: dynamic = MyClass()
    val res = a.field(B())
    return if (res.toString() == "A") "OK" else res.toString()
}
```

### Список для перегрузок
- Тест  overloads
- 1000 Int + 1000 String перемешанных для перегрузок

![!](https://pp.userapi.com/c840131/v840131398/1138/rjP69JbAl40.jpg)


### Сравнение реализаций кешей

- Тест overloads на 8 потоков
- 1000 Int + 1000 String перемешанных для перегрузок

- ReentrantLock + hashmap hashmap (log утерян, но внезапно нашлась фотография. а пересобирать смысла нет, появилось лучшее решение)

![ew](https://pp.userapi.com/c840131/v840131398/3ea/U-oj9uyC1ww.jpg)

- ReentrantLock + hashmap в CallSite

|Benchmark                                  |Cnt |     Score |      Error | Units|
|---|---|---|---|---|
|GroovyDynamicMethod0_0                     | 60 |     0.038 | ±    0.001 | us/op|
|GroovyDynamicMethod3_3                     | 60 |   981.233 | ±   14.712 | us/op|
|GroovyDynamicMethod5Proxy                  | 60 |  1567.074 | ±    5.654 | us/op|
|GroovyDynamicMethod5_10                    | 60 |  1648.115 | ±   38.835 | us/op|
|GroovyDynamicMethod5_2_default3Proxy       | 60 |  1405.636 | ±   14.019 | us/op|
|GroovyInvokeDynamicMethod0_0               | 60 |     0.013 | ±    0.001 | us/op|
|GroovyInvokeDynamicMethod3_3               | 60 | 41736.439 | ±  894.566 | us/op|
|GroovyInvokeDynamicMethod5Proxy            | 60 | 49150.659 | ± 2127.131 | us/op|
|GroovyInvokeDynamicMethod5_10              | 60 | 55677.995 | ± 1213.618 | us/op|
|GroovyInvokeDynamicMethod5_2_default3Proxy | 60 | 44745.824 | ±  883.252 | us/op|
|kotlinDynamicMethod0_0                     | 60 |     0.004 | ±    0.001 | us/op|
|kotlinDynamicMethod3_3                     | 60 |  1442.085 | ±   11.134 | us/op|
|kotlinDynamicMethod5Proxy                  | 60 |  1720.368 | ±   13.739 | us/op|
|kotlinDynamicMethod5_10                    | 60 |  1760.024 | ±   21.880 | us/op|
|kotlinDynamicMethod5_2_default3Proxy       | 60 |  1688.205 | ±    9.158 | us/op|

- Collections.synchronizedMap в CallSite.

|Benchmark                                  | Cnt |     Score |      Error | Units|
|---|---|---|---|---|
|GroovyDynamicMethod0_0                     |  60 |     0.038 | ±    0.001 | us/op|
|GroovyDynamicMethod3_3                     |  60 |   972.503 | ±    9.126 | us/op|
|GroovyDynamicMethod5Proxy                  |  60 |  1552.920 | ±   11.394 | us/op|
|GroovyDynamicMethod5_10                    |  60 |  1615.261 | ±    7.205 | us/op|
|GroovyDynamicMethod5_2_default3Proxy       |  60 |  1363.339 | ±   10.463 | us/op|
|GroovyInvokeDynamicMethod0_0               |  60 |     0.011 | ±    0.001 | us/op|
|GroovyInvokeDynamicMethod3_3               |  60 | 41110.996 | ±  438.299 | us/op|
|GroovyInvokeDynamicMethod5Proxy            |  60 | 54066.070 | ±  602.085 | us/op|
|GroovyInvokeDynamicMethod5_10              |  60 | 51602.621 | ± 1103.996 | us/op|
|GroovyInvokeDynamicMethod5_2_default3Proxy |  60 | 41187.940 | ±  801.785 | us/op|
|kotlinDynamicMethod0_0                     |  60 |     0.004 | ±    0.001 | us/op|
|kotlinDynamicMethod3_3                     |  60 |  1364.518 | ±   18.220 | us/op|
|kotlinDynamicMethod5Proxy                  |  60 |  1442.166 | ±   12.882 | us/op|
|kotlinDynamicMethod5_10                    |  60 |  1416.214 | ±   30.963 | us/op|
|kotlinDynamicMethod5_2_default3Proxy       |  60 |  1434.649 | ±   15.752 | us/op|
