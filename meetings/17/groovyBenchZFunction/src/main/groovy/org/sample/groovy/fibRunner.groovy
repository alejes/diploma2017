package org.sample.groovy

import groovy.transform.CompileStatic

/*class GroovyRunnerDynamicCompiler {
    static def z_function(s) {
        def n = s.length()
        def z = new ArrayList<Integer>(n)
        for (i in 1..n)
            z.add(0)
        def l = 0
        def r = 0
        for (i in 1..(n-1)) {
            if (i <= r)
                z[i] = Math.min(r - i + 1, z[i - l]);
            while (i + z[i] < n
                    && s[z[i]] == s[i + z[i]]) {
                z[i]++
            }
            if (i + z[i] - 1 > r)
                l = i
            r = i + z[i] - 1;
        }

        return z;
    }

    static def int maxRepeatablePrefix(String str) {
        def z = z_function(str)
        def max = z[0]
        for (element in z) {
            max = Math.max(max, element)
        }
        return (int) max
    }
}*/

@CompileStatic
class GroovyRunnerStaticCompiler {
    static List<Integer> z_function(String s) {
        def n = s.length()
        def z = new ArrayList<Integer>(n)
        for (i in 1..n)
            z.add(0)
        def l = 0
        def r = 0
        for (i in 1..(n-1)) {
            if (i <= r)
                z[i] = Math.min(r - i + 1, z[i - l]);
            while (i + z[i] < n
                    && s[z[i]] == s[i + z[i]]) {
                z[i]++
            }
            if (i + z[i] - 1 > r)
                l = i
            r = i + z[i] - 1;
        }

        return z;
    }

    static def int maxRepeatablePrefix(String str) {
        def z = z_function(str)
        def max = z[0]
        for (element in z) {
            max = Math.max(max, element)
        }
        return (int) max
    }
}


static main(String[] args) {
    String list = "abacaba"
    println(list)
    def result = GroovyRunnerStaticCompiler.maxRepeatablePrefix(list)
    println(result)

}
