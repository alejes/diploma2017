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
