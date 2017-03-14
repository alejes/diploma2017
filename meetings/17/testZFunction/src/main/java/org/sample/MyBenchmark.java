package org.sample;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.sample.groovy.GroovyRunnerDynamicCompiler;
import org.sample.groovy.GroovyRunnerInvokeDynamicCompiler;
import org.sample.groovy.GroovyRunnerStaticCompiler;
import org.sample.kotlin.KotlinRunnerDynamic;
import org.sample.kotlin.KotlinRunnerString;


import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class MyBenchmark {

    @Param({"100", "500", "1500"})
    private int n = 0;

    private String str = "";

    private Random rnd;


    @Setup(Level.Trial)
    public void prepare() {
        rnd = new Random(42);
        //System.out.println("list prepared");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i =0; i < n; ++i) {
            stringBuilder.append((char) ('A' + rnd.nextInt('Z'-'A')));
        }
        str = stringBuilder.toString();
    }


    @Benchmark
    public long kotlinStringDynamic() {
        return KotlinRunnerDynamic.Companion.maxRepeatablePrefix(str);
    }

    @Benchmark
    public long kotlinString() {
        return KotlinRunnerString.Companion.maxRepeatablePrefix(str);
    }

    @Benchmark
    public long groovyStringStatic() {
        return GroovyRunnerStaticCompiler.maxRepeatablePrefix(str);
    }

    @Benchmark
    public long groovyStringTraditional() {
        return GroovyRunnerDynamicCompiler.maxRepeatablePrefix(str);
    }

    @Benchmark
    public long groovyStringInvokeDynamic() {
        return GroovyRunnerInvokeDynamicCompiler.maxRepeatablePrefix(str);
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
