package generate;

import org.objectweb.asm.*;

import java.io.FileOutputStream;


public class PrintGen implements Opcodes {

    public static void main(String[] args) throws Exception {
        try (FileOutputStream fos = new FileOutputStream("src/main/depend/Print.class")) {
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

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "Print", null, "java/lang/Object", null);

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
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitLdcInsn("YOU WINN!!");
            mv.visitVarInsn(ASTORE, 1);
            mv.visitLdcInsn(Type.getType("Lkotlin/io/ConsoleKt;"));
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInvokeDynamicInsn("invoke", "(Ljava/lang/Class;Ljava/lang/Object;)V", new Handle(Opcodes.H_INVOKESTATIC, "kotlin/DynamicMetaFactory", "bootstrapDynamic", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I)Ljava/lang/invoke/CallSite;"), new Object[]{"println", new Integer(0)});
            mv.visitInsn(RETURN);

            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
