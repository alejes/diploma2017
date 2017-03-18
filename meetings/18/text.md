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
        55: ifeq          63
        58: pop2
        59: pop
        60: goto          69
        63: invokedynamic #57,  0             // InvokeDynamic #3:invoke:(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        68: pop
        69: return

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