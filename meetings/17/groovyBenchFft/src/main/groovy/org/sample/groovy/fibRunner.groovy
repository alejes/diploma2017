package org.sample.groovy

import groovy.transform.CompileStatic

import java.util.stream.Collector
import java.util.stream.Collectors


@CompileStatic
class MyComplexStatic {
    double re = 0
    double im = 0

    def MyComplexStatic(double re) {
        this.re = re
    }

    def MyComplexStatic(double re, double im) {
        this.re = re
        this.im = im
    }

    MyComplexStatic multiply(MyComplexStatic other) {
        return new MyComplexStatic(this.re * other.re - this.im * other.im,
                this.im * other.re + other.im * this.re)
    }

    MyComplexStatic plus(MyComplexStatic other) {
        return new MyComplexStatic(this.re + other.re, this.im + other.im)
    }

    MyComplexStatic minus(MyComplexStatic other) {
        return new MyComplexStatic(this.re - other.re, this.im - other.im)
    }

    MyComplexStatic div(MyComplexStatic other) {
        double re2 = other.re * other.re
        double im2 = other.im * other.im
        double denominator = re2 + im2

        return new MyComplexStatic((this.re * other.re + this.im * other.im) / denominator,
                (this.im * other.re - other.im * this.re) / denominator)
    }


    String toString() {
        return "{$re;$im}"
    }
}


@CompileStatic
class GroovyRunnerStatic {
    static fft(List<MyComplexStatic> a, boolean invert) {
        def n = a.size()
        if (n == 1) return
        def size = ((int)n).intdiv(2).intValue()
        def a0 = new ArrayList<MyComplexStatic>(size)
        for (i in 0..size-1) {
            a0.add(new MyComplexStatic(0.0))
        }
        def a1 = new ArrayList<MyComplexStatic>(size)
        for (i in 0..size-1) {
            a1.add(new MyComplexStatic(0.0))
        }
        def i = 0
        def j = 0
        while (i < n) {
            a0[j] = a[i]
            a1[j] = a[i + 1]
            i += 2
            ++j
        }
        fft(a0, invert)
        fft(a1, invert)
        def ang = 2 * Math.PI / n * ((invert) ? -1 : 1)
        def w = new MyComplexStatic(1.0)
        def wn = new MyComplexStatic(Math.cos(ang), Math.sin(ang))
        i = 0
        while (i < n / 2) {
            a[i] = a0[i] + w * a1[i]
            a[(i + (((int)n).intdiv(2))).intValue()] = a0[i] - w * a1[i]
            if (invert) {
                a[i] /= new MyComplexStatic(2.0)
                a[(i + (((int)n).intdiv(2))).intValue()] /= new MyComplexStatic(2.0)
            }
            w *= wn
            ++i
        }
    }

    static List<Double> fftProxy(List<Double> list) {
        List<MyComplexStatic> complexList = list.stream().map{ new MyComplexStatic(it)}.collect(Collectors.toList())
        fft(complexList, false)
        return complexList.stream().map { it.re }.collect(Collectors.toList())
    }
}



/*
static main(String[] args) {
    def list = new ArrayList<Integer>([1, 2, 3, 4, 5, 6, 7, 8])
    println(list)
    def result = GroovyRunnerStatic.fftProxy(list)
    println(result)
}*/