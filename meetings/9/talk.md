```
fun function1(x : String){
    println ("String")
}
fun function1(x : Int){
    println ("Int")
}
fun main(args: Array<String>) {
    val a : dynamic = 567;

    function1(a);
}
```


> Overload resolution ambiguity. All these functions match.

> public fun function1(x: Int): Unit defined in root package
  
> public fun function1(x: String): Unit defined in root package
