package kotlin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.net.BindException;

public abstract class DynamicSelector {

    public static DynamicSelector getMethodSelector(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) {
        return new FieldSelector(mc, caller, type, name, arguments);
    }

    public abstract void setCallSite() throws BindException;

    public abstract MethodHandle getMethodHandle();

    private static class FieldSelector extends DynamicSelector {
        private MutableCallSite mc;
        private MethodHandles.Lookup caller;
        private MethodType type;
        private String name;
        private Object[] arguments;
        private MethodHandle handle;

        private FieldSelector(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) {
            this.mc = mc;
            this.caller = caller;
            this.type = type;
            this.name = name;
            this.arguments = arguments;
        }

        public void setCallSite() throws BindException {
            //mc.setTarget(cal);
            int z = 4;
            genMethodClass();
            processSetCallSite();
        }


        private void processSetCallSite() {
            //cached in groovy
            handle = MethodHandles.explicitCastArguments(handle, type);
            mc.setTarget(handle);
        }

        private void genMethodClass() throws BindException {
            Object receiver = arguments[0];
            if (receiver == null) {
                throw new UnsupportedOperationException("null");
            } else if (receiver instanceof Class) {
                throw new UnsupportedOperationException("null");
            } else {
                try {
                    handle = caller.unreflectGetter(receiver.getClass().getField(name));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new BindException(e.getMessage());
                }
            }
        }

        @Override
        public MethodHandle getMethodHandle() {
            return handle;
        }
    }
}
