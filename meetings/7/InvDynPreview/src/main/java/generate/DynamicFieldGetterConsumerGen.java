package generate;

import org.objectweb.asm.*;

import java.io.FileOutputStream;


public class DynamicFieldGetterConsumerGen implements Opcodes {

    public static void main(String[] args) throws Exception {
        try (FileOutputStream fos = new FileOutputStream("src/main/depend/FGetter.class")) {
            for (short _byte: dump()) {
                fos.write(_byte);
            }
        }
    }

    public static byte[] dump() throws Exception {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "FGetter", null, "java/lang/Object", null);

        {
            fv = cw.visitField(ACC_PUBLIC, "myField", "I", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PUBLIC, "myStringField", "Ljava/lang/String;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_5);
            mv.visitFieldInsn(PUTFIELD, "FGetter", "myField", "I");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn("Privet");
            mv.visitFieldInsn(PUTFIELD, "FGetter", "myStringField", "Ljava/lang/String;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitTypeInsn(NEW, "FGetter");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "FGetter", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInvokeDynamicInsn("myField", "(LFGetter;)I", new Handle(Opcodes.H_INVOKESTATIC, "kotlin/DynamicMetaFactory", "bootstrapDynamicFieldGetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"), new Object[]{});
            //mv.visitFieldInsn(GETFIELD, "FGetter", "myField", "I");
            mv.visitVarInsn(ISTORE, 2);

            mv.visitVarInsn(ALOAD, 1);
            mv.visitInvokeDynamicInsn("myStringField", "(LFGetter;)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "kotlin/DynamicMetaFactory", "bootstrapDynamicFieldGetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"), new Object[]{});
            ///mv.visitInvokeDynamicInsn("myStringField", "(LFGetter;)Ljava/lang/Object;", new Handle(Opcodes.H_INVOKESTATIC, "kotlin/DynamicMetaFactory", "bootstrapDynamicFieldGetter", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"), new Object[]{});
            //mv.visitFieldInsn(GETFIELD, "FGetter", "myStringField", "Ljava/lang/String;");
            mv.visitVarInsn(ASTORE, 3);

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(ALOAD, 3);
            //mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);


            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 4);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
