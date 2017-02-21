package com.jenkov

import org.openjdk.jmh.annotations.Benchmark
import org.ope  njdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit

import java.util.concurrent.TimeUnit

class MyBenchmark {

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MINUTES)
    fun testMethod() {
        // This is a demo/sample template for building your JMH benchmarks. Edit as needed.
        // Put your benchmark code here.

        val a = 1
        val b = 2
        val sum = a + b
    }

}