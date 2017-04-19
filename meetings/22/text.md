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


А что с полями? Там вообще неразбериха! Идея - давайте возвращать то поле, которое наиболее близкое в иерархии наследовании к данному классу.
