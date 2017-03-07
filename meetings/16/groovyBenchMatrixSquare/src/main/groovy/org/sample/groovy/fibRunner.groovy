package org.sample.groovy

import java.util.stream.Collectors

import groovy.transform.CompileStatic

@CompileStatic
class GroovyRunnerStaticCompiler {
    static List<List<Integer>> matrixSquare(List<List<Integer>> source) {
        def destination = new ArrayList<ArrayList<Integer>>();
        for (i in 0..source.size()-1) {
            destination.add(new ArrayList<Integer>())
            for (j in 0..source.size()-1) {
                destination[i].add(j, 0)
            }
        }


        for (i in 0..source.size()-1) {
            for (j in 0..source.size()-1) {
                for (k in 0..source.size()-1) {
                    def temp = source[i][k] * source[k][j];
                    destination[i][j] = temp + destination[i][j];
                }
            }
        }

        return destination;
    }

    static List<List<Integer>> matrixSquareProxy(List<List<Integer>> source) {
        return matrixSquare(source)
    }
}

static main(String[] args) {
    int n = 5;
    List<List<Integer>> list = new ArrayList<>(n);
    Random rnd = new Random()
    for (int i =0; i < n; ++i) {
        List <Integer> temp = rnd.ints().limit(n).boxed().collect(Collectors.toList())
        list.add(temp);
    }
    println(list)
    def result = GroovyRunnerStaticCompiler.matrixSquareProxy(list)
    println(result)
}