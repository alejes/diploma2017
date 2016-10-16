# Сравнение производительности Dynamic vs NotDynamic

|Compiler | [Time per iteration](http://blog.dhananjaynene.com/2008/07/performance-comparison-c-java-python-ruby-jython-jruby-groovy/) | [Fib(48)](https://gist.github.com/chanwit/133661) [Mail](http://www.mail-archive.com/mlvm-dev@openjdk.java.net/msg00821.html) |
|---|---| --- |
| Indy     |  23.903433212 microseconds | 98932.222809 ms |
| Standart |  22.292255485 microseconds | 248350.093248 ms |
| Static | ~ | 18076.178766 ms |

### Gradual typing
Прочитал: http://wphomes.soic.indiana.edu/jsiek/what-is-gradual-typing/
