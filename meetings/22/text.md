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
    ICONST_5
    ANEWARRAY java/lang/Integer
    DUP
    ICONST_0
    ICONST_1
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    AASTORE
    DUP
    ICONST_1
    ICONST_2
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    AASTORE
    DUP
    ICONST_2
    ICONST_3
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    AASTORE
    DUP
    ICONST_3
    ICONST_4
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    AASTORE
    DUP
    ICONST_4
    ICONST_5
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    AASTORE
    INVOKESTATIC kotlin/collections/CollectionsKt.listOf ([Ljava/lang/Object;)Ljava/util/List;
    ASTORE 0
   L1
    LINENUMBER 1206 L1
    ALOAD 0
    ICONST_0
    INVOKEINTERFACE java/util/List.get (I)Ljava/lang/Object;
    CHECKCAST java/lang/Number
    INVOKEVIRTUAL java/lang/Number.intValue ()I
    ISTORE 1
   L2
    LINENUMBER 1207 L2
    ALOAD 0
    INVOKEINTERFACE java/util/List.iterator ()Ljava/util/Iterator;
    ASTORE 3
   L3
    ALOAD 3
    INVOKEINTERFACE java/util/Iterator.hasNext ()Z
    IFEQ L4
    ALOAD 3
    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object;
    CHECKCAST java/lang/Number
    INVOKEVIRTUAL java/lang/Number.intValue ()I
    ISTORE 2
   L5
    LINENUMBER 1208 L5
   L6
    ILOAD 1
    ILOAD 2
    INVOKESTATIC java/lang/Math.max (II)I
   L7
    ISTORE 1
```

```
kotlin.DynamicBindException: class is not public: java.util.Arrays$ArrayList.get(int)Object/invokeVirtual, from ListIteratorKt
```

Короткий пример проблемы:

```
import java.util.AbstractList;
import java.util.List;


public class MySuperClass {
    private static class MyList extends AbstractList {
        @Override
        public Object get(int index) {
            return 5557;
        }

        @Override
        public int size() {
            return 0;
        }
    }

    List toList() {
        return new MyList();
    }
}
```


А что с полями? Там вообще неразбериха! ~~Идея - давайте возвращать то поле, которое наиболее близкое в иерархии наследовании к данному классу.~~ это проблема аналогична тому что мы не знаем статический тип, и мы ничего здесь не сделаем.

Для методов:
- Мы можем сказать что вы ССЗБ что используете тут динамики и эта та же самая проблема что разрешение A a = new B();
- Можно сказать что если мы не нашли этот метод с плохими правами доступа к этому классу, то давайте поищем среди интерфейсов или родителей такой класс, что мы можем получить к нему доступ.


### Groovy

[MetaMethodIndex.java](https://github.com/groovy/groovy-core/blob/master/src/main/org/codehaus/groovy/runtime/metaclass/MetaMethodIndex.java#L372)
                    // do not overwrite interface methods with instance methods
                    // do not overwrite private methods
                    // Note: private methods from parent classes are not shown here,
                    // but when doing the multimethod connection step, we overwrite
                    // methods of the parent class with methods of a subclass and
                    // in that case we want to keep the private methods
