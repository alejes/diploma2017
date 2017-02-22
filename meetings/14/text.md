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

### Обидное
Не верифицируется фибоначчи
```
fun fib(n: Long): Long =
        if(n < 2)
            n
        else
            fib(n-1) + fib(n-2)
```

### Тест
```
class ManyDynamicCalls() {
    companion object {
        @JvmStatic
        fun functionWithManyDynamicCalls(n: dynamic): dynamic {
            var computedValue: dynamic = 5
            for (i in 1..n) {
                computedValue = mySqrt(n)
            }
            return computedValue
        }

        @JvmStatic
        fun mySqrt(x: dynamic): dynamic {
            return Math.sqrt(x);
        }

        @JvmStatic
        fun runManyDynamicTest(n: Int): Double {
            return functionWithManyDynamicCalls(n)
        }
    }
}
```
- dynamic
```
 java -jar target/benchmarks.jar -i 5 -wi 5 -f 1
# JMH 1.6 (released 739 days ago, please consider updating!)
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: <none>
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.jetbrains.benchmarks.TestRunner.benchmarkMethod
# Parameters: (n = 10)

# Run progress: 0.00% complete, ETA 00:00:40
# Fork: 1 of 1
# Warmup Iteration   1: 101.408 ns/op
# Warmup Iteration   2: 87.257 ns/op
# Warmup Iteration   3: 89.897 ns/op
# Warmup Iteration   4: 81.767 ns/op
# Warmup Iteration   5: 79.852 ns/op
Iteration   1: 82.279 ns/op
Iteration   2: 83.234 ns/op
Iteration   3: 82.056 ns/op
Iteration   4: 82.697 ns/op
Iteration   5: 82.887 ns/op


Result: 82.630 ±(99.9%) 1.815 ns/op [Average]
  Statistics: (min, avg, max) = (82.056, 82.630, 83.234), stdev = 0.471
  Confidence interval (99.9%): [80.815, 84.446]


# JMH 1.6 (released 739 days ago, please consider updating!)
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: <none>
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.jetbrains.benchmarks.TestRunner.benchmarkMethod
# Parameters: (n = 20)

# Run progress: 25.00% complete, ETA 00:00:32
# Fork: 1 of 1
# Warmup Iteration   1: 215.052 ns/op
# Warmup Iteration   2: 176.449 ns/op
# Warmup Iteration   3: 177.991 ns/op
# Warmup Iteration   4: 186.190 ns/op
# Warmup Iteration   5: 170.328 ns/op
Iteration   1: 167.650 ns/op
Iteration   2: 163.438 ns/op
Iteration   3: 167.045 ns/op
Iteration   4: 163.975 ns/op
Iteration   5: 166.697 ns/op


Result: 165.761 ±(99.9%) 7.378 ns/op [Average]
  Statistics: (min, avg, max) = (163.438, 165.761, 167.650), stdev = 1.916
  Confidence interval (99.9%): [158.383, 173.139]


# JMH 1.6 (released 739 days ago, please consider updating!)
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: <none>
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.jetbrains.benchmarks.TestRunner.benchmarkMethod
# Parameters: (n = 30)

# Run progress: 50.00% complete, ETA 00:00:21
# Fork: 1 of 1
# Warmup Iteration   1: 313.641 ns/op
# Warmup Iteration   2: 267.905 ns/op
# Warmup Iteration   3: 258.237 ns/op
# Warmup Iteration   4: 281.757 ns/op
# Warmup Iteration   5: 246.405 ns/op
Iteration   1: 245.090 ns/op
Iteration   2: 253.071 ns/op
Iteration   3: 253.638 ns/op
Iteration   4: 250.160 ns/op
Iteration   5: 251.482 ns/op


Result: 250.688 ±(99.9%) 13.147 ns/op [Average]
  Statistics: (min, avg, max) = (245.090, 250.688, 253.638), stdev = 3.414
  Confidence interval (99.9%): [237.541, 263.835]


# JMH 1.6 (released 739 days ago, please consider updating!)
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: <none>
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.jetbrains.benchmarks.TestRunner.benchmarkMethod
# Parameters: (n = 50)

# Run progress: 75.00% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 490.332 ns/op
# Warmup Iteration   2: 433.711 ns/op
# Warmup Iteration   3: 417.072 ns/op
# Warmup Iteration   4: 438.428 ns/op
# Warmup Iteration   5: 407.813 ns/op
Iteration   1: 412.546 ns/op
Iteration   2: 405.750 ns/op
Iteration   3: 407.511 ns/op
Iteration   4: 405.518 ns/op
Iteration   5: 396.382 ns/op


Result: 405.541 ±(99.9%) 22.527 ns/op [Average]
  Statistics: (min, avg, max) = (396.382, 405.541, 412.546), stdev = 5.850
  Confidence interval (99.9%): [383.015, 428.068]


# Run complete. Total time: 00:00:42

Benchmark                   (n)  Mode  Cnt    Score    Error  Units
TestRunner.benchmarkMethod   10  avgt    5   82.630 ±  1.815  ns/op
TestRunner.benchmarkMethod   20  avgt    5  165.761 ±  7.378  ns/op
TestRunner.benchmarkMethod   30  avgt    5  250.688 ± 13.147  ns/op
TestRunner.benchmarkMethod   50  avgt    5  405.541 ± 22.527  ns/op
```
- static
```
# JMH 1.6 (released 739 days ago, please consider updating!)
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: <none>
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.jetbrains.benchmarks.TestRunner.benchmarkMethod
# Parameters: (n = 10)

# Run progress: 0.00% complete, ETA 00:00:40
# Fork: 1 of 1
# Warmup Iteration   1: 6.596 ns/op
# Warmup Iteration   2: 6.617 ns/op
# Warmup Iteration   3: 6.496 ns/op
# Warmup Iteration   4: 6.503 ns/op
# Warmup Iteration   5: 6.566 ns/op
Iteration   1: 6.583 ns/op
Iteration   2: 6.492 ns/op
Iteration   3: 6.513 ns/op
Iteration   4: 6.436 ns/op
Iteration   5: 6.466 ns/op


Result: 6.498 ±(99.9%) 0.214 ns/op [Average]
  Statistics: (min, avg, max) = (6.436, 6.498, 6.583), stdev = 0.055
  Confidence interval (99.9%): [6.284, 6.712]


# JMH 1.6 (released 739 days ago, please consider updating!)
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: <none>
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.jetbrains.benchmarks.TestRunner.benchmarkMethod
# Parameters: (n = 20)

# Run progress: 25.00% complete, ETA 00:00:31
# Fork: 1 of 1
# Warmup Iteration   1: 6.530 ns/op
# Warmup Iteration   2: 6.603 ns/op
# Warmup Iteration   3: 6.587 ns/op
# Warmup Iteration   4: 6.401 ns/op
# Warmup Iteration   5: 6.536 ns/op
Iteration   1: 6.544 ns/op
Iteration   2: 6.458 ns/op
Iteration   3: 6.423 ns/op
Iteration   4: 6.425 ns/op
Iteration   5: 6.434 ns/op


Result: 6.457 ±(99.9%) 0.196 ns/op [Average]
  Statistics: (min, avg, max) = (6.423, 6.457, 6.544), stdev = 0.051
  Confidence interval (99.9%): [6.261, 6.652]


# JMH 1.6 (released 739 days ago, please consider updating!)
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: <none>
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.jetbrains.benchmarks.TestRunner.benchmarkMethod
# Parameters: (n = 30)

# Run progress: 50.00% complete, ETA 00:00:21
# Fork: 1 of 1
# Warmup Iteration   1: 6.465 ns/op
# Warmup Iteration   2: 6.527 ns/op
# Warmup Iteration   3: 6.549 ns/op
# Warmup Iteration   4: 6.564 ns/op
# Warmup Iteration   5: 6.439 ns/op
Iteration   1: 6.448 ns/op
Iteration   2: 6.462 ns/op
Iteration   3: 6.375 ns/op
Iteration   4: 6.448 ns/op
Iteration   5: 6.450 ns/op


Result: 6.437 ±(99.9%) 0.134 ns/op [Average]
  Statistics: (min, avg, max) = (6.375, 6.437, 6.462), stdev = 0.035
  Confidence interval (99.9%): [6.302, 6.571]


# JMH 1.6 (released 739 days ago, please consider updating!)
# VM invoker: /usr/lib/jvm/java-8-oracle/jre/bin/java
# VM options: <none>
# Warmup: 5 iterations, 1 s each
# Measurement: 5 iterations, 1 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.jetbrains.benchmarks.TestRunner.benchmarkMethod
# Parameters: (n = 50)

# Run progress: 75.00% complete, ETA 00:00:10
# Fork: 1 of 1
# Warmup Iteration   1: 6.497 ns/op
# Warmup Iteration   2: 6.447 ns/op
# Warmup Iteration   3: 6.540 ns/op
# Warmup Iteration   4: 6.467 ns/op
# Warmup Iteration   5: 6.462 ns/op
Iteration   1: 6.632 ns/op
Iteration   2: 6.406 ns/op
Iteration   3: 6.392 ns/op
Iteration   4: 6.413 ns/op
Iteration   5: 6.494 ns/op


Result: 6.467 ±(99.9%) 0.385 ns/op [Average]
  Statistics: (min, avg, max) = (6.392, 6.467, 6.632), stdev = 0.100
  Confidence interval (99.9%): [6.082, 6.853]


# Run complete. Total time: 00:00:41

Benchmark                   (n)  Mode  Cnt  Score   Error  Units
TestRunner.benchmarkMethod   10  avgt    5  6.498 ± 0.214  ns/op
TestRunner.benchmarkMethod   20  avgt    5  6.457 ± 0.196  ns/op
TestRunner.benchmarkMethod   30  avgt    5  6.437 ± 0.134  ns/op
TestRunner.benchmarkMethod   50  avgt    5  6.467 ± 0.385  ns/op
```

### Посмотреть
- ConstantExprEval
- 1::minus<>
- Написать про не собирающиеся box тесты
