package org.sample.groovy

import groovy.transform.CompileStatic


class GroovyRunnerIntInvokeDynamic {
    static fib(n) {
        if (n < 2) {
            return n
        } else {
            return fib(n - 1) + fib(n - 2)
        }
    }

    static int fibProxy(int n) {
        return (int)fib(n)
    }
}
