### Unit всё ок 
### operator plusAssign +=
```
class ListenerList<T> : AbstractMutableList<T>() {
    private val list = mutableListOf<T>()
    override fun add(index: Int, element: T) {
        list.add(index, element)
    }

    override fun removeAt(index: Int): T
            = list.removeAt(index)


    override fun set(index: Int, element: T): T {
        println("set call")
        return list.set(index, element);
    }

    override val size: Int
        get() = list.size

    override fun get(index: Int): T {
        println("get call")
        return list.get(index)
    }
}

class MyObject {
    operator fun plusAssign(other: MyObject) {
        println("call plusAssign")
        //return other
    }
}



fun main(args: Array<String>) {
    val z: dynamic = ListenerList<MyObject>()
    z.add(MyObject())
    z[0] += MyObject()
}
```

```
get call
call plusAssign
```

```
 0: aload_0
         1: ldc           #9                  // String args
         3: invokestatic  #15                 // Method kotlin/jvm/internal/Intrinsics.checkParameterIsNotNull:(Ljava/lang/Object;Ljava/lang/String;)V
         6: new           #17                 // class ListenerList
         9: dup
        10: invokespecial #21                 // Method ListenerList."<init>":()V
        13: astore_1
        14: aload_1
        15: new           #23                 // class MyObject
        18: dup
        19: invokespecial #24                 // Method MyObject."<init>":()V
        22: invokedynamic #38,  0             // InvokeDynamic #0:invoke:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        27: pop
        28: aload_1
        29: iconst_0
        30: invokestatic  #44                 // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        33: dup2
        34: invokedynamic #47,  0             // InvokeDynamic #1:invoke:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        39: new           #23                 // class MyObject
        42: dup
        43: invokespecial #24                 // Method MyObject."<init>":()V
        46: invokedynamic #50,  0             // InvokeDynamic #2:invoke:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        51: dup
        52: instanceof    #52                 // class kotlin/DynamicMetaFactory$AssignmentMarker
        55: ifeq          64
        58: pop
        59: pop
        60: pop
        61: goto          70
        64: invokedynamic #57,  0             // InvokeDynamic #3:invoke:(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        69: pop
        70: return
```


### Что с мапой и plusAssign?

```
var operationsCount = 0

class ListenerMap<K, V> : AbstractMutableMap<K, V>() {
    private val map = mutableMapOf<K, V>()

    override val size: Int
        get() = map.size

    override fun put(key: K, value: V): V? {
        operationsCount += 1
        return map.put(key, value)!!
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = map.entries

    override fun get(key: K): V? {
        operationsCount += 100
        return map.get(key)
    }
}


class MyObject (var value: Int) {
    operator fun plus(other: MyObject) : MyObject{
        //value += other.value
        return this
    }
}




fun box(): String {
    val z = ListenerMap<Long, MyObject>()
    z.put(5L, MyObject(15))
    z[5L]!! += MyObject(11)
    val result1 = if (z[5L]!!.value == 26) "O" else "FAIL"
    val result2 = if (operationsCount == 201) "K" else "FAIL"
    return result1 + result2
}

fun main(args: Array<String>) {
    println(box())
}
```


### Сейчас для += и мапы генерируется такой код:
```
   L3
    NEW MyObject
    DUP
    BIPUSH 11
    INVOKESPECIAL MyObject.<init> (I)V
    INVOKEDYNAMIC invoke(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; [
      // handle kind 0x6 : INVOKESTATIC
      kotlin/DynamicMetaFactory.bootstrapDynamic(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I[Ljava/lang/String;)Ljava/lang/invoke/CallSite;
      // arguments:
      "plusAssign", 
      0
    ]
    DUP
    INSTANCEOF kotlin/DynamicMetaFactory$AssignmentMarker
    IFEQ L4
    POP
    GOTO L5
   L4
    NEW kotlin/DynamicBindException
    DUP
    LDC "Cannot find selector"
    INVOKESPECIAL kotlin/DynamicBindException.<init> (Ljava/lang/String;)V
    ATHROW
   L5

```


### А что если

Вместо
```
z.put(5L, MyObject(15))
```
Будет 
```
z[5L] = MyObject(15)
```

Мы в рантайме не сможем найти метод set!

Потому что для мапы генерируется код 
```
INVOKEINTERFACE java/util/Map.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
```

### А что у нас с mapой и plus//plusAssign

```
var operationsCount = 0

class ListenerMap<K, V> : AbstractMutableMap<K, V>() {
    private val map = mutableMapOf<K, V>()

    override val size: Int
        get() = map.size

    override fun put(key: K, value: V): V? {
        operationsCount += 1
        return map.put(key, value)
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = map.entries

    override fun get(key: K): V? {
        operationsCount += 100
        return map.get(key)
    }

    fun set(key: K, value: V) {
        operationsCount += 10000
        map.set(key, value)
    }
}


class MyObject (var value: Int) {
    operator fun plus (other: MyObject): MyObject {
        value += other.value
        return this
    }
    
/*operator fun plusAssign (other: MyObject) {
        value += other.value
    }*/

}




fun box(): String {
    val z = ListenerMap<Long, MyObject>()
    z[5L] = MyObject(15)
    z[5L]!! += MyObject(11)
    val result1 = if (z[5L]!!.value == 26) "O" else "FAIL"
    val result2 = if (operationsCount == 201) "K" else "FAIL"
    return result1 + result2
}
```


### Немного вопросов
- Если мы в рантайме получили именованный аргумент, но не нашли его у целевой функции - надо ли падать с ошибкой?
- Должно ли имя параметра влиять на разрешение перегрузок


### Groovy
- Используем org.codehaus.groovy.reflection
- Не мешает ли jmh оптимизации блокировок?
```
org.codehaus.groovy.reflection
    public final MetaClass getMetaClass() {
        MetaClass answer = getMetaClassForClass();
        if (answer != null) return answer;

        lock();
        try {
            return getMetaClassUnderLock();
        } finally {
            unlock();
        }
    }
```
- Создаём метаклассы внутри для каждого класса. Особенно примечателен там класс FastArray.
- Обходим всех родителей создавая полный индекс методов
- Делаем некоторые неявные конверсии
```
* There are some conversions we have to do explicitly.
* These are GString to String, Number to Byte and Number to BigInteger
* conversions.
```
- Null receiver
```
* Gives a replacement receiver for null.
* In case of the receiver being null we want to do the method
* invocation on NullObject instead.
```
```NullObject.getNullObject()```

- Обрамляем эксепшены через ```MethodHandles.catchException``` 

```added GroovyRuntimeException unwrapper```

```
* Adds the standard exception handler.  
         */
        public void addExceptionHandler() {
            //TODO: if we would know exactly which paths require the exceptions
            //      and which paths not, we can sometimes save this guard 
            if (handle==null || catchException==false) return;
            Class returnType = handle.type().returnType();
            if (returnType!=Object.class) {
                MethodType mtype = MethodType.methodType(returnType, GroovyRuntimeException.class); 
                handle = MethodHandles.catchException(handle, GroovyRuntimeException.class, UNWRAP_EXCEPTION.asType(mtype));
            } else {
                handle = MethodHandles.catchException(handle, GroovyRuntimeException.class, UNWRAP_EXCEPTION);
            }
            if (LOG_ENABLED) LOG.info("added GroovyRuntimeException unwrapper");
        }
```


```
    //  --------------------------------------------------------
    //                   exception handling
    //  --------------------------------------------------------
    public static Throwable unwrap(GroovyRuntimeException gre) {
        if (gre.getCause()==null) {
            if (gre instanceof MissingPropertyExceptionNoStack) {
                MissingPropertyExceptionNoStack noStack = (MissingPropertyExceptionNoStack) gre;
                return new MissingPropertyException(noStack.getProperty(), noStack.getType());
            }

            if (gre instanceof MissingMethodExceptionNoStack) {
                MissingMethodExceptionNoStack noStack = (MissingMethodExceptionNoStack) gre;
                return new MissingMethodException(noStack.getMethod(), noStack.getType(), noStack.getArguments(), noStack.isStatic());
            }
        }

        Throwable th = gre;
        if (th.getCause() != null && th.getCause() != gre) th = th.getCause();
        if (th != gre && (th instanceof GroovyRuntimeException)) return unwrap((GroovyRuntimeException) th);
        return th;
    }
   ```
   
   
   
### fix problems with nulls in dynamic function arguments


### Случай
```
val a: dynamic = A()
a.call()
```
Что такое call?
Сейчас мы проверяем на функцию call => на друга-оператора присваивания.

Но!
```
class A {
    public val field1 = { "OK" }
}
```

Или экстремальный случай - лямбда от большого числа аргументов.
Тут надо гуардить изменения ресивера (как объекта, а не только класса), лямбды (как объекта), аргументов (как классов).
Также надо руками искать поле с именем ```get + Capitalized(name)``` руками подбирать его тип, в ```<receiver, FunctionN>``` и дёргать потом ```invoke```.


### Обогатил коллекцию тестов
Итого 29 штук

### Выбор перегрузок

```
class Overloads {
    fun method0_1(): String {
        return "OK"
    }

    fun method3_3(x: Int, y: Int, z: Int): String {
        return x.toString() + y.toString() + z.toString()
    }
    fun method3_3(x: Int, y: String, z: Int): String {
        return x.toString() + y.toString() + z.toString()
    }
    fun method3_3(x: String, y: String, z: Int): String {
        return x.toString() + y.toString() + z.toString()
    }

    fun method5_5(x: Int, y: Int, z: Int, u: Int, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_5(x: Int, y: String, z: Int, u: String, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_5(x: Int, y: Int, z: String, u: Int, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_5(x: Int, y: String, z: Int, u: Int, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_5(x: String, y: Int, z: Int, u: Int, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }

    fun method5_1_default3(x: Int, y: Int, z: Int = 654, u: Int = 46, v: Int = 54): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }


    fun method5_10(x: Int, y: Int, z: Int, u: Int, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_10(x: Int, y: String, z: Int, u: String, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_10(x: String, y: Int, z: Int, u: Int, v: String): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_10(x: Int, y: String, z: Int, u: Int, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_10(x: Int, y: Int, z: String, u: Int, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_10(x: Int, y: Int, z: String, u: String, v: String): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_10(x: Int, y: Int, z: Int, u: String, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_10(x: Int, y: String, z: Int, u: Int, v: String): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_10(x: String, y: Int, z: Int, u: Int, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
    fun method5_10(x: String, y: String, z: String, u: Int, v: Int): String {
        return x.toString() + y.toString() + z.toString() + u.toString() + v.toString()
    }
}
```
```
class KotlinRunnerDynamic {
    companion object {
        @JvmStatic
        fun method0_0Proxy(): String {
            val x: dynamic = Overloads()
            return x.method0_1()
        }

        @JvmStatic
        fun method3_3Proxy(arg1: Int): String {
            val x: dynamic = Overloads()
            return x.method3_3(5, "eew", arg1)
        }

        @JvmStatic
        fun method5_5Proxy(arg1: Int): String {
            val x: dynamic = Overloads()
            return x.method5_5(11, "33d", 5, "eew", arg1)
        }

        @JvmStatic
        fun method5_1_default3Proxy(arg1: Int): String {
            val x: dynamic = Overloads()
            return x.method5_1_default3(11, arg1)
        }

        @JvmStatic
        fun method5_10Proxy(arg1: Int): String {
            val x: dynamic = Overloads()
            return x.method5_10(11, "33d", arg1, "eew", 55)
        }
    }
}
```

|Benchmark|Arguments_Overloads|Mode|Cnt|Score|Error|Units|Units|
|---|---|---|---|---|---|---|---|
|KotlinStaticMethod|0_1|avgt|60|3|±  0,001|us/op|us/op|
|kotlinDynamicMethod|0_1|avgt|60|3|±  0,001|us/op|us/op|
|GroovyStaticMethod|0_1|avgt|60|5|±  0,001|us/op|us/op|
|GroovyInvokeDynamicMethod|0_1|avgt|60|7|±  0,001|us/op|us/op|
|GroovyDynamicMethod|0_1|avgt|60|20|±  0,001|us/op|us/op|
|KotlinStaticMethod|3_3|avgt|60|38|±  0,001|us/op|us/op|
|kotlinDynamicMethod|3_3|avgt|60|41|±  0,001|us/op|us/op|
|GroovyStaticMethod|3_3|avgt|60|57|±  0,001|us/op|us/op|
|kotlinDynamicMethod|5_5|avgt|60|78|±  0,002|us/op|us/op|
|kotlinDynamicMethod|5_10|avgt|60|80|±  0,002|us/op|us/op|
|KotlinStaticMethod|5_5|avgt|60|97|±  0,002|us/op|us/op|
|KotlinStaticMethod|5_10|avgt|60|98|±  0,002|us/op|us/op|
|GroovyDynamicMethod|3_3|avgt|60|106|±  0,002|us/op|us/op|
|kotlinDynamicMethod|5_1 (3 of 5 is default)|avgt|60|109|±  0,003|us/op|us/op|
|GroovyInvokeDynamicMethod|3_3|avgt|60|112|±  0,003|us/op|us/op|
|KotlinStaticMethod|5_1 (3 of 5 is default)|avgt|60|116|±  0,002|us/op|us/op|
|GroovyStaticMethod|5_10|avgt|60|153|±  0,003|us/op|us/op|
|GroovyStaticMethod|5_5|avgt|60|157|±  0,004|us/op|us/op|
|GroovyInvokeDynamicMethod|5_5|avgt|60|184|±  0,004|us/op|us/op|
|GroovyInvokeDynamicMethod|5_10|avgt|60|194|±  0,004|us/op|us/op|
|GroovyStaticMethod|5_1 (3 of 5 is default)|avgt|60|207|±  0,006|us/op|us/op|
|GroovyDynamicMethod|5_5|avgt|60|228|±  0,005|us/op|us/op|
|GroovyDynamicMethod|5_10|avgt|60|230|±  0,005|us/op|us/op|
|GroovyInvokeDynamicMethod|5_1 (3 of 5 is default)|avgt|60|235|±  0,006|us/op|us/op|
|GroovyDynamicMethod|5_1 (3 of 5 is default)|avgt|60|259|±  0,006|us/op|us/op|


```
CompoundAssignmentPerform
```
