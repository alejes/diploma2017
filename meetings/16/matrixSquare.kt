class KotlinRunner {
    companion object {
        @JvmStatic
        fun matrixSquare(n: dynamic): dynamic {
            val x: MutableList<List<Int>> = mutableListOf<List<Int>>();
            for (i in 0..n.size-1) {
                x.add(MutableList(n.size, { _ -> 46  }))
            }
            return x;
        }

        @JvmStatic
        fun matrixSquareProxy(source: List<List<Int>>): List<List<Int>> {
            //return MutableList<List<Int>>(5, { _ -> MutableList(5, { _ -> 5 }) });
            return matrixSquare(source)
        }
    }
}

fun main(args: Array<String>) {
    val z = List<List<Int>>(5, {_->List(5, {_-> 53})})
    val y = KotlinRunner.matrixSquareProxy(z)
    for (i in 0..y  .size-1){
        for (j in 0..y[i].size-1){
            print(y[i][j])
        }
        println();
    }
}
