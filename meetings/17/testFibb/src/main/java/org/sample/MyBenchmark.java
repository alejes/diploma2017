package org.sample;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.sample.groovy.GroovyRunnerLongDynamic;
import org.sample.groovy.GroovyRunnerLongInvokeDynamic;
import org.sample.groovy.GroovyRunnerLongStatic;
import org.sample.kotlin.KotlinRunnerLong;
import org.sample.kotlin.KotlinRunnerLongDynamic;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MyBenchmark {

    @Param({"10", "20", "30"})
    private int n = 0;


    @Benchmark
    public long kotlinFibDynamic() {
        return KotlinRunnerLongDynamic.Companion.fibProxy(n);
    }

    @Benchmark
    public long kotlinFibLong() {
        return KotlinRunnerLong.Companion.fibProxy(n);
    }

    @Benchmark
    public long groovyFibLongStatic() {
        return GroovyRunnerLongStatic.fibProxy(n);
    }

    @Benchmark
    public long groovyFibTraditional() {
        return GroovyRunnerLongDynamic.fibProxy(n);
    }

    @Benchmark
    public long groovyFibInvokeDynamic() {
        return GroovyRunnerLongInvokeDynamic.fibProxy(n);
    }

/*
    @Benchmark
    public int javaFused() {
        int res = 0;

        return res;
    }
*/

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(5)
                .forks(1)
                .jvmArgs("-Xmx16m")
                .build();

        new Runner(opt).run();
    }

}
