# Groovy

### Типы кодогенерации
- стандартная - ужасная ```Array<CallSite>```
- Java-like ```CompileStatic```
- InDy

### Иерархия вызова в стандартном Groovy
![class calls](http://i.stack.imgur.com/gVoUQ.png)

### [GroovyInterceptable](http://docs.groovy-lang.org/latest/html/api/groovy/lang/GroovyInterceptable.html)
Marker interface used to notify that all methods should be intercepted through the invokeMethod mechanism of GroovyObject.
```
class SelfAwareness{
    static void main (String[] args){
        Interceptable machine = new Interceptable();
        machine.reactTo("hello!")
    }

}
class Interceptable implements GroovyInterceptable{
    void reactTo(Object message){
        System.out.println "beep..."
    }

    @Override
    Object invokeMethod(String methodName, Object args) {
        System.out.println("Called ${this.class.name}.$methodName  with $args")
        MetaMethod method = Interceptable.metaClass.getMetaMethod(methodName, args)
        method.invoke(this, args)
    }
}

```
```
Called Interceptable.reactTo  with [hello!]
beep...
```

### Как заиспользовать InvokeDynamic в Groovy?

```compile 'org.codehaus.groovy:groovy-all:2.4.3:```__indy__```'```

```
tasks.withType(GroovyCompile) {
    groovyOptions.optimizationOptions.indy = true
}
```

### Примеры кода
__Input__
```
class Person {
    def bar() {
        Object x = new Foo()
        x = x.myFooBar()
        return x
    }
}
```
__Output__
```
  public bar()Ljava/lang/Object;
   L0
    LINENUMBER 3 L0
    LDC LFoo;.class
    INVOKEDYNAMIC init(Ljava/lang/Class;)Ljava/lang/Object; [
      // handle kind 0x6 : INVOKESTATIC
      org/codehaus/groovy/vmplugin/v7/IndyInterface.bootstrap(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I)Ljava/lang/invoke/CallSite;
      // arguments:
      "<init>", 
      0
    ]
    ASTORE 1
   L1
    ALOAD 1
    POP
   L2
    LINENUMBER 4 L2
    ALOAD 1
    INVOKEDYNAMIC invoke(Ljava/lang/Object;)Ljava/lang/Object; [
      // handle kind 0x6 : INVOKESTATIC
      org/codehaus/groovy/vmplugin/v7/IndyInterface.bootstrap(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I)Ljava/lang/invoke/CallSite;
      // arguments:
      "myFooBar", 
      0
    ]
    ASTORE 2
    ALOAD 2
    ASTORE 1
    ALOAD 2
    POP
   L3
    LINENUMBER 5 L3
    ALOAD 1
    ARETURN
   L4
   FRAME FULL [] [java/lang/Throwable]
    NOP
    ATHROW
    LOCALVARIABLE this LPerson; L0 L4 0
    LOCALVARIABLE x Ljava/lang/Object; L1 L4 1
    MAXSTACK = 1
    MAXLOCALS = 3
```

#### class [IndyInterface.bootstrap](http://docs.groovy-lang.org/2.4.0/html/api/org/codehaus/groovy/vmplugin/v7/IndyInterface.html#bootstrap(java.lang.invoke.MethodHandles.Lookup,%20java.lang.String,%20java.lang.invoke.MethodType))
```
public static CallSite bootstrap(MethodHandles.Lookup caller,
                 String name,
                 MethodType type)
```
_Deprecated. since Groovy 2.1.0_
__bootstrap method for standard method calls__

```
/**
         * bootstrap method for method calls from Groovy compiled code with indy 
         * enabled. This method gets a flags parameter which uses the following 
         * encoding:<ul>
         * <li>{@value #SAFE_NAVIGATION} is the flag value for safe navigation see {@link #SAFE_NAVIGATION}<li/>
         * <li>{@value #THIS_CALL} is the flag value for a call on this see {@link #THIS_CALL}</li>
         * </ul> 
         * @param caller - the caller
         * @param callType - the type of the call
         * @param type - the call site type
         * @param name - the real method name
         * @param flags - call flags
         * @return the produced CallSite
         * @since Groovy 2.1.0
         */
        public static CallSite bootstrap(Lookup caller, String callType, MethodType type, String name, int flags) {
            boolean safe = (flags&SAFE_NAVIGATION)!=0;
            boolean thisCall = (flags&THIS_CALL)!=0;
            boolean spreadCall = (flags&SPREAD_CALL)!=0;
            int callID;
            if (callType.equals(CALL_TYPES.METHOD.getCallSiteName())) {
                callID = CALL_TYPES.METHOD.ordinal();
            } else if (callType.equals(CALL_TYPES.INIT.getCallSiteName())) {
                callID = CALL_TYPES.INIT.ordinal();
            } else if (callType.equals(CALL_TYPES.GET.getCallSiteName())) {
                callID = CALL_TYPES.GET.ordinal();
            } else if (callType.equals(CALL_TYPES.SET.getCallSiteName())) {
                callID = CALL_TYPES.SET.ordinal();
            } else if (callType.equals(CALL_TYPES.CAST.getCallSiteName())) {
                callID = CALL_TYPES.CAST.ordinal();
            }else {
                throw new GroovyBugError("Unknown call type: "+callType);
            }
            return realBootstrap(caller, name, callID, type, safe, thisCall, spreadCall);
        }
```

```
/**
         * backing bootstrap method with all parameters
         */
        private static CallSite realBootstrap(Lookup caller, String name, int callID, MethodType type, boolean safe, boolean thisCall, boolean spreadCall) {
            // since indy does not give us the runtime types
            // we produce first a dummy call site, which then changes the target to one,
            // that does the method selection including the the direct call to the 
            // real method.
            MutableCallSite mc = new MutableCallSite(type);
            MethodHandle mh = makeFallBack(mc,caller.lookupClass(),name,callID,type,safe,thisCall,spreadCall);
            mc.setTarget(mh);
            return mc;
        }
``` 
```
 /**
         * Makes a fallback method for an invalidated method selection
         */
        protected static MethodHandle makeFallBack(MutableCallSite mc, Class<?> sender, String name, int callID, MethodType type, boolean safeNavigation, boolean thisCall, boolean spreadCall) {
            MethodHandle mh = MethodHandles.insertArguments(SELECT_METHOD, 0, mc, sender, name, callID, safeNavigation, thisCall, spreadCall, /*dummy receiver:*/ 1);
            mh =    mh.asCollector(Object[].class, type.parameterCount()).
                    asType(type);
            return mh;
        }
```

```
private static final MethodHandle SELECT_METHOD = LOOKUP.findStatic(IndyInterface.class, "selectMethod", mt);
```

```
/**
         * Core method for indy method selection using runtime types.
         */
        public static Object selectMethod(MutableCallSite callSite, Class sender, String methodName, int callID, Boolean safeNavigation, Boolean thisCall, Boolean spreadCall, Object dummyReceiver, Object[] arguments) throws Throwable {
            Selector selector = Selector.getSelector(callSite, sender, methodName, callID, safeNavigation, thisCall, spreadCall, arguments); 
            selector.setCallSiteTarget();

            MethodHandle call = selector.handle.asSpreader(Object[].class, arguments.length);
            call = call.asType(MethodType.methodType(Object.class,Object[].class));
            return call.invokeExact(arguments);
        }
```
