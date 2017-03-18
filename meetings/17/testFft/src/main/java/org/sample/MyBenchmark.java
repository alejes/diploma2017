package org.sample;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.sample.groovy.GroovyRunnerDynamic;
import org.sample.groovy.GroovyRunnerInvokeDynamic;
import org.sample.groovy.GroovyRunnerStatic;
import org.sample.kotlin.KotlinRunnerDynamic;
import org.sample.kotlin.KotlinRunnerStatic;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MyBenchmark {

    @Param({"8", "16", "32", "64"})
    private int n = 0;

    private List<Double> list;
    private Random rnd = new Random();

    @Setup(Level.Trial)
    public void prepare() {
        rnd = new Random(42);
        list = rnd.doubles().limit(n).boxed().collect(Collectors.toList());
    }


    @Benchmark
    public List<Double> kotlinFftDynamic() {
        return KotlinRunnerDynamic.Companion.fftProxy(list);
    }

    @Benchmark
    public List<Double> kotlinFftLong() {
        return KotlinRunnerStatic.Companion.fftProxy(list);
    }

    @Benchmark
    public List<Double> groovyFftStatic() {
        return GroovyRunnerStatic.fftProxy(list);
    }

    @Benchmark
    public List<Double> groovyFftTraditional() {
        return GroovyRunnerDynamic.fftProxy(list);
    }

    @Benchmark
    public List<Double> groovyFftInvokeDynamic() {
        return GroovyRunnerInvokeDynamic.fftProxy(list);
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
