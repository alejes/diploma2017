package generate;

import org.objectweb.asm.*;

import java.io.FileOutputStream;


public class DynamicFieldGetterConsumerGen implements Opcodes {

    public static void main(String[] args) throws Exception {
        try (FileOutputStream fos = new FileOutputStream("src/main/depend/FielID.class")) {
            for (short _byte : dump()) {
                fos.write(_byte);
            }
        }
    }

    public static byte[] dump() throws Exception {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "FielID", null, "java/lang/Object", null);

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "doWork", "(Ljava/lang/Object;)V", null, null);
            mv.visitCode();

            mv.visitVarInsn(ALOAD, 0);
            mv.visitInvokeDynamicInsn("getProperty",
                    "(Ljava/lang/Object;)Ljava/lang/Object;",
                    new Handle(Opcodes.H_INVOKESTATIC, "kotlin/DynamicMetaFactory", "bootstrapDynamic",
                            "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I)Ljava/lang/invoke/CallSite;"),
                    new Object[]{"myStringField", new Integer(0)});
            mv.visitVarInsn(ASTORE, 1);

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 1);
            //mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false);
            //mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "TargetClass");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "TargetClass", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "FielID", "doWork", "(Ljava/lang/Object;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "msain", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "FGetter");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "FGetter", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInvokeDynamicInsn("getProperty", "(Ljava/lang/Object;)Ljava/lang/Object;", new Handle(Opcodes.H_INVOKESTATIC, "kotlin/DynamicMetaFactory", "bootstrapDynamic", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I)Ljava/lang/invoke/CallSite;"), new Object[]{"myStringField", new Integer(0)});
            //mv.visitFieldInsn(GETFIELD, "FGetter", "myField", "I");
            mv.visitVarInsn(ASTORE, 2);


            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);


            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 4);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
