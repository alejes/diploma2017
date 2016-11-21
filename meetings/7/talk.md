### Проблемы
- Несовпадение типов
  - boxed/unboxed int
  - Поле объявлено
```
public class IDDL {
  public String myStringField = "Privet";
}
IDDL inst = new IDDL();
Object v = inst.myStringField;
System.out.println(v);
```
```
mv.visitVarInsn(ALOAD, 1);
mv.visitFieldInsn(GETFIELD, "IDDL", "myStringField", "Ljava/lang/String;");
mv.visitVarInsn(ASTORE, 3);

mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
mv.visitVarInsn(ALOAD, 3);
mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
```



```
mv.visitInvokeDynamicInsn("myStringField", "(LFGetter;)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "kotlin/DynamicMetaFactory", "bootstrapDynamicFieldGetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"), new Object[]{});
mv.visitInvokeDynamicInsn("myStringField", "(LFGetter;)Ljava/lang/Object;", new Handle(Opcodes.H_INVOKESTATIC, "kotlin/DynamicMetaFactory", "bootstrapDynamicFieldGetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"), new Object[]{});
```
- Exception in thread "main" java.lang.BootstrapMethodError: call site initialization exception
- Caused by: java.lang.invoke.WrongMethodTypeException: MethodHandle(FGetter)String should be of type (FGetter)Object
