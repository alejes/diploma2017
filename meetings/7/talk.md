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
