### Groovy
- При установке Property, Groovy не делает indy, MetaClassRegistryImpl
```
    Code:
      stack=4, locals=3, args_size=1
         0: ldc           #57                 // String er3ewew
         2: astore_1      
         3: aload_1       
         4: aconst_null   
         5: aload_0       
         6: ldc           #40                 // String myStringField
         8: invokestatic  #63                 // Method org/codehaus/groovy/runtime/ScriptBytecodeAdapter.setProperty:(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;)V
        11: aload_1       
        12: pop           
        13: aload_0       
        14: invokedynamic #45,  0             // InvokeDynamic #0:getProperty:(Ljava/lang/Object;)Ljava/lang/Object;
        19: astore_2      
        20: aload_2       
        21: pop           
        22: ldc           #2                  // class strCaller
        24: aload_2       
        25: invokedynamic #51,  0             // InvokeDynamic #1:invoke:(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;
        30: pop           
        31: return  
```
```
public static void doStoreWork(obj){
        obj.myStringField = "er3ewew"
        def res = obj.myStringField;
        print res
    }
```
Он оборачивает объект своей обёрткой:
> A registry of MetaClass instances which caches introspection & reflection information and allows methods to be dynamically added to existing classes at runtime

```
    public static void setProperty(Object object, String property, Object newValue) {
        if (object == null) {
            object = NullObject.getNullObject();
        }

        if (object instanceof GroovyObject) {
            GroovyObject pogo = (GroovyObject) object;
            pogo.setProperty(property, newValue);
        } else if (object instanceof Class) {
            metaRegistry.getMetaClass((Class) object).setProperty((Class) object, property, newValue);
        } else {
            ((MetaClassRegistryImpl) GroovySystem.getMetaClassRegistry()).getMetaClass(object).setProperty(object, property, newValue);
        }
    }
```
