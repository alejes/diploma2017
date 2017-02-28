//package org.jetbrains.benchmarks.dynamic


/*class ManyDynamicCalls() {
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
}*/

/*
class ManyDynamicCalls() {
    companion object {
        @JvmStatic
        fun functionWithManyDynamicCalls(n: Int): Double {
            var computedValue: Double = 5.0
            for (i in 1..n) {
                computedValue = mySqrt(n)
            }
            return computedValue
        }

        @JvmStatic
        fun mySqrt(x: Int): Double {
            return Math.sqrt(x.toDouble());
        }

        @JvmStatic
        fun runManyDynamicTest(n: Int): Double {
            return functionWithManyDynamicCalls(n)
        }
    }
}
*/

/*

fun fib(n: dynamic): dynamic =
        if(n < 2)
            n
        else
            fib(n-1) + fib(n-2)

fun main(args: Array<String>) {
    println (fib(23))
    //val x: dynamic = 5
    //println(x + x)
}
*/


/*
fun  func(cx: dynamic) {
    for (x in cx){
        println(x)
    }
}


fun main(args: Array<String>) {
    val x = IntArray(10, {it * it})
    func(x)
}
        */


/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


//import java.nio.charset.Charset


/**
 * Returns a *typed* array containing all of the elements of this collection.
 *
 * Allocates an array of runtime type `T` having its size equal to the size of this collection
 * and populates the array with the elements of this collection.
 */
/*
public inline fun <reified T> Collection<T>.toTypedArray(): Array<T> {
    val thisCollection = this as java.util.Collection<T>
    return thisCollection.toArray(arrayOfNulls<T>(thisCollection.size())) as Array<T>
}
*/

/*
fun jsz(z: String): dynamic {
    return {it: dynamic -> 5;}
}
class A {
    override fun hashCode(): Int = jsz("Kotlin.identityHashCode")(this)
}
*/


/*class A {
    fun method1(x: Int) : Int = 5
    fun method1(x: String) : Int = 23

}



*//*
public fun String.toDouble(): Double = (+(this.asDynamic())).unsafeCast<Double>().also {
    if (it.isNaN() && !this.isNaN() || it == 0.0 && this.isBlank())
        numberFormatError(this)
}
*//*


fun main(args: Array<String>) {
    val x: dynamic = A()
    println(x.method1("2253"))
}*/


/*

class A {
    fun method1(x: Int) {

    }

    fun method1(x: String) {

    }
}

fun main(args: Array<String>) {
    val x = A()
    val z: dynamic = 33
    x.method1(z)
}

 */


package org.sample.kotlin

class KotlinRunnerDynamic {
    companion object {
        @JvmStatic
        fun fib(n: dynamic): dynamic {
            if (n < 2) {
                return n
            } else {
                return fib(n - 1) + fib(n - 2)
            }
        }
        @JvmStatic
        fun fibProxy(n: Int): Int {
            return fib(n)
        }
    }
}

class KotlinRunnerInt {
    companion object {
        @JvmStatic
        fun fib(n: Int): Int {
            if (n < 2) {
                return n
            } else {
                return fib(n - 1) + fib(n - 2)
            }
        }
        @JvmStatic
        fun fibProxy(n: Int): Int {
            return fib(n)
        }
    }
}
