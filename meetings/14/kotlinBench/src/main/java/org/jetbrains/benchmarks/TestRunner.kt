package org.jetbrains.benchmarks

import org.jetbrains.benchmarks.dynamic.ManyDynamicCalls
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.RunnerException
import org.openjdk.jmh.runner.options.Options
import org.openjdk.jmh.runner.options.OptionsBuilder

import java.util.Random
import java.util.concurrent.TimeUnit

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
open public class TestRunner {
    @Param("10", "20", "30", "50")
    var n: Int = 0

    @Setup
    public fun init() {
    }



    @Benchmark
    public fun benchmarkMethod(bh: Blackhole) {
        val res = ManyDynamicCalls.runManyDynamicTest(n)
        bh.consume(res)
    }
}
