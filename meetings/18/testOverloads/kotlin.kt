package org.sample.kotlin

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

class KotlinRunner {
    companion object {
        @JvmStatic
        fun method0_0Proxy(): String {
            val x = Overloads()
            return x.method0_1()
        }

        @JvmStatic
        fun method3_3Proxy(arg1: Int): String {
            val x = Overloads()
            return x.method3_3(5, "eew", arg1)
        }

        @JvmStatic
        fun method5_5Proxy(arg1: Int): String {
            val x = Overloads()
            return x.method5_5(11, "33d", arg1, "eew", 55)
        }

        @JvmStatic
        fun method5_1_default3Proxy(arg1: Int): String {
            val x = Overloads()
            return x.method5_1_default3(11, arg1)
        }

        @JvmStatic
        fun method5_10Proxy(arg1: Int): String {
            val x = Overloads()
            return x.method5_10(11, "33d", arg1, "eew", 55)
        }
    }
}


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
