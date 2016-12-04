package generate;

import java.io.FileOutputStream;
import java.util.*;
import org.objectweb.asm.*;
public class KotlinPrintLnGen implements Opcodes {

    public static void main(String[] args) throws Exception {
        try (FileOutputStream fos = new FileOutputStream("src/main/depend/PRINTLN.class")) {
            for (short _byte: dump()) {
                fos.write(_byte);
            }
        }
    }

    public static byte[] dump () throws Exception {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(52, ACC_PUBLIC + ACC_SUPER, "PRINTLN", null, "java/lang/Object", null);

        {
            av0 = cw.visitAnnotation("Lkotlin/Metadata;", true);
            av0.visit("mv", new int[] {1,1,2});
            av0.visit("bv", new int[] {1,0,1});
            av0.visit("k", new Integer(2));
            {
                AnnotationVisitor av1 = av0.visitArray("d1");
                av1.visit(null, "\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\u001a\u0019\u0010\u0000\u001a\u00020\u00012\u000c\u0010\u0002\u001a\u0008\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\u0010\u0005");
                av1.visitEnd();
            }
            {
                AnnotationVisitor av1 = av0.visitArray("d2");
                av1.visit(null, "main");
                av1.visit(null, "");
                av1.visit(null, "args");
                av1.visit(null, "");
                av1.visit(null, "");
                av1.visit(null, "([Ljava/lang/String;)V");
                av1.visitEnd();
            }
            av0.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            {
                av0 = mv.visitParameterAnnotation(0, "Lorg/jetbrains/annotations/NotNull;", false);
                av0.visitEnd();
            }
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn("args");
            mv.visitMethodInsn(INVOKESTATIC, "kotlin/jvm/internal/Intrinsics", "checkParameterIsNotNull", "(Ljava/lang/Object;Ljava/lang/String;)V", false);
            mv.visitLdcInsn("YOU WINN!!");
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInvokeDynamicInsn("invoke", "(Ljava/lang/Object;)V", new Handle(Opcodes.H_INVOKESTATIC, "kotlin/DynamicMetaFactory", "bootstrapDynamic", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I)Ljava/lang/invoke/CallSite;"), new Object[]{"println", new Integer(0)});
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}