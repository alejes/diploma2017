package org.sample;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.sample.groovy.GrooveRunnerDynamic;
import org.sample.groovy.GrooveRunnerInvokeDynamic;
import org.sample.groovy.GrooveRunnerStatic;
import org.sample.kotlin.KotlinRunnerDynamic;
import org.sample.kotlin.KotlinRunner;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MyBenchmark {

    //@Param({"10", "20", "30"})
    private int n = 5;


    @Benchmark
    public String kotlinDynamicMethod0_0() {
        return KotlinRunnerDynamic.method0_0Proxy();
    }
    @Benchmark
    public String kotlinDynamicMethod3_3() {
        return KotlinRunnerDynamic.method3_3Proxy(n);
    }
    @Benchmark
    public String kotlinDynamicMethod5_1_default3Proxy() {
        return KotlinRunnerDynamic.method5_1_default3Proxy(n);
    }
    @Benchmark
    public String kotlinDynamicMethod5Proxy() {
        return KotlinRunnerDynamic.method5_5Proxy(n);
    }
    @Benchmark
    public String kotlinDynamicMethod5_10() {
        return KotlinRunnerDynamic.method5_10Proxy(n);
    }


    @Benchmark
    public String KotlinStaticMethod0_0() {
        return KotlinRunner.method0_0Proxy();
    }
    @Benchmark
    public String KotlinStaticMethod3_3() {
        return KotlinRunner.method3_3Proxy(n);
    }
    @Benchmark
    public String KotlinStaticMethod5_1_default3Proxy() {
        return KotlinRunner.method5_1_default3Proxy(n);
    }
    @Benchmark
    public String KotlinStaticMethod5Proxy() {
        return KotlinRunner.method5_5Proxy(n);
    }
    @Benchmark
    public String KotlinStaticMethod5_10() {
        return KotlinRunner.method5_10Proxy(n);
    }

    
    @Benchmark
    public String GroovyStaticMethod0_0() {
        return GrooveRunnerStatic.method0_0Proxy();
    }
    @Benchmark
    public String GroovyStaticMethod3_3() {
        return GrooveRunnerStatic.method3_3Proxy(n);
    }
    @Benchmark
    public String GroovyStaticMethod5_1_default3Proxy() {
        return GrooveRunnerStatic.method5_1_default3Proxy(n);
    }
    @Benchmark
    public String GroovyStaticMethod5Proxy() {
        return GrooveRunnerStatic.method5_5Proxy(n);
    }
    @Benchmark
    public String GroovyStaticMethod5_10() {
        return GrooveRunnerStatic.method5_10Proxy(n);
    }


    @Benchmark
    public String GroovyDynamicMethod0_0() {
        return GrooveRunnerDynamic.method0_0Proxy();
    }
    @Benchmark
    public String GroovyDynamicMethod3_3() {
        return GrooveRunnerDynamic.method3_3Proxy(n);
    }
    @Benchmark
    public String GroovyDynamicMethod5_1_default3Proxy() {
        return GrooveRunnerDynamic.method5_1_default3Proxy(n);
    }
    @Benchmark
    public String GroovyDynamicMethod5Proxy() {
        return GrooveRunnerDynamic.method5_5Proxy(n);
    }
    @Benchmark
    public String GroovyDynamicMethod5_10() {
        return GrooveRunnerDynamic.method5_10Proxy(n);
    }




    @Benchmark
    public String GroovyInvokeDynamicMethod0_0() {
        return GrooveRunnerInvokeDynamic.method0_0Proxy();
    }
    @Benchmark
    public String GroovyInvokeDynamicMethod3_3() {
        return GrooveRunnerInvokeDynamic.method3_3Proxy(n);
    }
    @Benchmark
    public String GroovyInvokeDynamicMethod5_1_default3Proxy() {
        return GrooveRunnerInvokeDynamic.method5_1_default3Proxy(n);
    }
    @Benchmark
    public String GroovyInvokeDynamicMethod5Proxy() {
        return GrooveRunnerInvokeDynamic.method5_5Proxy(n);
    }
    @Benchmark
    public String GroovyInvokeDynamicMethod5_10() {
        return GrooveRunnerInvokeDynamic.method5_10Proxy(n);
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
