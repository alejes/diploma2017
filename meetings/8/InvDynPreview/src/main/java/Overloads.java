/*
import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class Overloads implements Opcodes {

    public static void main(String[] args) throws Exception {
        try (FileOutputStream fos = new FileOutputStream("OverloadsStatic.class")) {
            for (short _byte : dump())
                fos.write(_byte);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static byte[] dump() throws Exception {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "org/sample/groovy/OverloadsStatic", null, "java/lang/Object", new String[]{"groovy/lang/GroovyObject"});

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "$staticClassInfo", "Lorg/codehaus/groovy/reflection/ClassInfo;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PUBLIC + ACC_STATIC + ACC_TRANSIENT + ACC_SYNTHETIC, "__$stMC", "Z", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_TRANSIENT + ACC_SYNTHETIC, "metaClass", "Lgroovy/lang/MetaClass;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "$staticClassInfo$", "Lorg/codehaus/groovy/reflection/ClassInfo;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PUBLIC + ACC_STATIC + ACC_SYNTHETIC, "__timeStamp", "J", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PUBLIC + ACC_STATIC + ACC_SYNTHETIC, "__timeStamp__239_neverHappen1494510582390", "J", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/sample/groovy/OverloadsStatic", "$getStaticMetaClass", "()Lgroovy/lang/MetaClass;", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(SWAP);
            mv.visitFieldInsn(PUTFIELD, "org/sample/groovy/OverloadsStatic", "metaClass", "Lgroovy/lang/MetaClass;");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(POP);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method0_1", "()I", null, null);
            mv.visitCode();
            mv.visitIntInsn(BIPUSH, 111);
            mv.visitInsn(IRETURN);
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method3_3", "(III)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method3_3", "(ILjava/lang/String;I)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method3_3", "(Ljava/lang/String;Ljava/lang/String;I)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_5", "(IIIII)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_5", "(ILjava/lang/String;ILjava/lang/String;I)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            //// mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_5", "(IILjava/lang/String;II)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_5", "(ILjava/lang/String;III)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_5", "(Ljava/lang/String;IIII)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_1_default3", "(IIIII)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_10", "(IIIII)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_10", "(ILjava/lang/String;ILjava/lang/String;I)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_10", "(Ljava/lang/String;IIILjava/lang/String;)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_10", "(ILjava/lang/String;III)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_10", "(IILjava/lang/String;II)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_10", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_10", "(IIILjava/lang/String;I)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_10", "(ILjava/lang/String;IILjava/lang/String;)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_10", "(Ljava/lang/String;IIII)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_10", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            // mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(2, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PROTECTED + ACC_SYNTHETIC, "$getStaticMetaClass", "()Lgroovy/lang/MetaClass;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitLdcInsn(Type.getType("Lorg/sample/groovy/OverloadsStatic;"));
            Label l0 = new Label();
            mv.visitJumpInsn(IF_ACMPEQ, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/ScriptBytecodeAdapter", "initMetaClass", "(Ljava/lang/Object;)Lgroovy/lang/MetaClass;", false);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitFieldInsn(GETSTATIC, "org/sample/groovy/OverloadsStatic", "$staticClassInfo", "Lorg/codehaus/groovy/reflection/ClassInfo;");
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            Label l1 = new Label();
            mv.visitJumpInsn(IFNONNULL, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/reflection/ClassInfo", "getClassInfo", "(Ljava/lang/Class;)Lorg/codehaus/groovy/reflection/ClassInfo;", false);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitFieldInsn(PUTSTATIC, "org/sample/groovy/OverloadsStatic", "$staticClassInfo", "Lorg/codehaus/groovy/reflection/ClassInfo;");
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"org/codehaus/groovy/reflection/ClassInfo"}, 0, null);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/codehaus/groovy/reflection/ClassInfo", "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "this$dist$invoke$1", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitLdcInsn(Type.getType("Lorg/sample/groovy/OverloadsStatic;"));
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "org/codehaus/groovy/runtime/GStringImpl");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitLdcInsn("");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/codehaus/groovy/runtime/GStringImpl", "<init>", "([Ljava/lang/Object;[Ljava/lang/String;)V", false);
            mv.visitInvokeDynamicInsn("cast", "(Lgroovy/lang/GString;)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "org/codehaus/groovy/vmplugin/v7/IndyInterface", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I)Ljava/lang/invoke/CallSite;"), new Object[]{"()", new Integer(0)});
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(AASTORE);
            mv.visitInsn(ICONST_1);
            mv.visitIntInsn(NEWARRAY, T_INT);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(IASTORE);
            mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/ScriptBytecodeAdapter", "despreadList", "([Ljava/lang/Object;[Ljava/lang/Object;[I)[Ljava/lang/Object;", false);
            mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/ScriptBytecodeAdapter", "invokeMethodOnCurrentN", "(Ljava/lang/Class;Lgroovy/lang/GroovyObject;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(ARETURN);
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(9, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "this$dist$set$1", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitLdcInsn(Type.getType("Lorg/sample/groovy/OverloadsStatic;"));
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "org/codehaus/groovy/runtime/GStringImpl");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitLdcInsn("");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/codehaus/groovy/runtime/GStringImpl", "<init>", "([Ljava/lang/Object;[Ljava/lang/String;)V", false);
            mv.visitInvokeDynamicInsn("cast", "(Lgroovy/lang/GString;)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "org/codehaus/groovy/vmplugin/v7/IndyInterface", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I)Ljava/lang/invoke/CallSite;"), new Object[]{"()", new Integer(0)});
            mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/ScriptBytecodeAdapter", "setGroovyObjectProperty", "(Ljava/lang/Object;Ljava/lang/Class;Lgroovy/lang/GroovyObject;Ljava/lang/String;)V", false);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(POP);
            mv.visitInsn(RETURN);
            mv.visitMaxs(10, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "this$dist$get$1", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitLdcInsn(Type.getType("Lorg/sample/groovy/OverloadsStatic;"));
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "org/codehaus/groovy/runtime/GStringImpl");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn("");
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitLdcInsn("");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESPECIAL, "org/codehaus/groovy/runtime/GStringImpl", "<init>", "([Ljava/lang/Object;[Ljava/lang/String;)V", false);
            mv.visitInvokeDynamicInsn("cast", "(Lgroovy/lang/GString;)Ljava/lang/String;", new Handle(Opcodes.H_INVOKESTATIC, "org/codehaus/groovy/vmplugin/v7/IndyInterface", "bootstrap", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;I)Ljava/lang/invoke/CallSite;"), new Object[]{"()", new Integer(0)});
            mv.visitMethodInsn(INVOKESTATIC, "org/codehaus/groovy/runtime/ScriptBytecodeAdapter", "getGroovyObjectProperty", "(Ljava/lang/Class;Lgroovy/lang/GroovyObject;Ljava/lang/String;)Ljava/lang/Object;", false);
            mv.visitInsn(ARETURN);
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(9, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_1_default3", "(IIII)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitIntInsn(BIPUSH, 54);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/sample/groovy/OverloadsStatic", "method5_1_default3", "(IIIII)I", false);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(6, 5);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_1_default3", "(III)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitIntInsn(BIPUSH, 46);
            mv.visitIntInsn(BIPUSH, 54);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/sample/groovy/OverloadsStatic", "method5_1_default3", "(IIIII)I", false);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(6, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method5_1_default3", "(II)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitIntInsn(SIPUSH, 654);
            mv.visitIntInsn(BIPUSH, 46);
            mv.visitIntInsn(BIPUSH, 54);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/sample/groovy/OverloadsStatic", "method5_1_default3", "(IIIII)I", false);
            mv.visitInsn(IRETURN);
            */
/*Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitInsn(NOP);
            mv.visitInsn(NOP);
            mv.visitInsn(ATHROW);*//*

            mv.visitMaxs(6, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "getMetaClass", "()Lgroovy/lang/MetaClass;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/sample/groovy/OverloadsStatic", "metaClass", "Lgroovy/lang/MetaClass;");
            mv.visitInsn(DUP);
            Label l0 = new Label();
            mv.visitJumpInsn(IFNULL, l0);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l0);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"groovy/lang/MetaClass"});
            mv.visitInsn(POP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/sample/groovy/OverloadsStatic", "$getStaticMetaClass", "()Lgroovy/lang/MetaClass;", false);
            mv.visitFieldInsn(PUTFIELD, "org/sample/groovy/OverloadsStatic", "metaClass", "Lgroovy/lang/MetaClass;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "org/sample/groovy/OverloadsStatic", "metaClass", "Lgroovy/lang/MetaClass;");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "setMetaClass", "(Lgroovy/lang/MetaClass;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "org/sample/groovy/OverloadsStatic", "metaClass", "Lgroovy/lang/MetaClass;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "invokeMethod", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/sample/groovy/OverloadsStatic", "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "groovy/lang/MetaClass", "invokeMethod", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(4, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "getProperty", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/sample/groovy/OverloadsStatic", "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEINTERFACE, "groovy/lang/MetaClass", "getProperty", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", true);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "setProperty", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/sample/groovy/OverloadsStatic", "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEINTERFACE, "groovy/lang/MetaClass", "setProperty", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V", true);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC + ACC_SYNTHETIC, "__$swapInit", "()V", null, null);
            mv.visitCode();
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            mv.visitMethodInsn(INVOKESTATIC, "org/sample/groovy/OverloadsStatic", "__$swapInit", "()V", false);
            mv.visitInsn(LCONST_0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            mv.visitVarInsn(ASTORE, 0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            mv.visitFieldInsn(PUTSTATIC, "org/sample/groovy/OverloadsStatic", "__timeStamp__239_neverHappen1494510582390", "J");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(POP);
            mv.visitLdcInsn(new Long(1494510582390L));
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            mv.visitFieldInsn(PUTSTATIC, "org/sample/groovy/OverloadsStatic", "__timeStamp", "J");
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(POP);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$notify", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "notify", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$hashCode", "()I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "hashCode", "()I", false);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$toString", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$clone", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "clone", "()Ljava/lang/Object;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$wait", "(JI)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(LLOAD, 1);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "wait", "(JI)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$wait", "(J)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(LLOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "wait", "(J)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$wait", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "wait", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$notifyAll", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "notifyAll", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$equals", "(Ljava/lang/Object;)Z", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$finalize", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "finalize", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNTHETIC, "super$1$getClass", "()Ljava/lang/Class;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC + ACC_SYNTHETIC, "class$", "(Ljava/lang/String;)Ljava/lang/Class;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            mv.visitTryCatchBlock(l0, l1, l1, "java/lang/ClassNotFoundException");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/ClassNotFoundException"});
            mv.visitVarInsn(ASTORE, 1);
            mv.visitTypeInsn(NEW, "java/lang/NoClassDefFoundError");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/ClassNotFoundException", "getMessage", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/NoClassDefFoundError", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
}
*/
