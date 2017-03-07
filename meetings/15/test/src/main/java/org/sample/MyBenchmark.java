package org.sample;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.sample.groovy.GroovyRunnerIntDynamic;
import org.sample.groovy.GroovyRunnerIntInvokeDynamic;
import org.sample.groovy.GroovyRunnerStaticCompiler;
import org.sample.kotlin.KotlinRunnerDynamic;
import org.sample.kotlin.KotlinRunnerInteger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MyBenchmark {

    @Param({"10", "20", "30"})
    private int n = 0;

    private List<List<Integer>> list;
    private Random rnd = new Random();

    @Setup(Level.Trial)
    public void prepare() {
        System.out.println("list prepared");
        list = new ArrayList<>(n);
        for (int i =0; i < n; ++i) {
            list.add(rnd.ints().limit(n).boxed().collect(Collectors.toList()));
        }
    }


    /*@Benchmark
    public List<List<Integer>> kotlinDynamic() {
        return KotlinRunnerDynamic.Companion.matrixSquareProxy(list);
    }*/

    @Benchmark
    public List<List<Integer>> kotlinInt() {
        return KotlinRunnerInteger.Companion.matrixSquareProxy(list);
    }

    /*@Benchmark
    public List<List<Integer>> groovyIntStatic() {
        return GroovyRunnerStaticCompiler.matrixSquareProxy(list);
    }

    @Benchmark
    public List<List<Integer>> groovyTraditional() {
        return GroovyRunnerIntDynamic.matrixSquareProxy(list);
    }

    @Benchmark
    public List<List<Integer>> groovyInvokeDynamic() {
        return GroovyRunnerIntInvokeDynamic.matrixSquareProxy(list);
    }*/

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
