import org.w3c.workers.ServiceWorkerState

fun myprint(x: Int) {}
fun myprint(x: Long) {}
fun myprint(x: String) {}
fun myprint(x: Byte) {}
fun myprint(x: dynamic) {}

fun main(args: Array<String>) {
    //val z = 5.asDynamic().unsafeCast<ServiceWorkerState>()
    //println(5.asDynamic())
    val z: dynamic = 5;
    myprint(z)
}