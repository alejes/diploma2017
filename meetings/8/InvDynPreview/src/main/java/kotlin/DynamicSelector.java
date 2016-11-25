package kotlin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Field;
import java.net.BindException;

public abstract class DynamicSelector {

    public static DynamicSelector getMethodSelector(MutableCallSite mc,
                                                    MethodHandles.Lookup caller,
                                                    MethodType type,
                                                    String name,
                                                    Object[] arguments,
                                                    DynamicMetaFactory.INVOKE_TYPE it) {
        return new FieldSelector(mc, caller, type, name, arguments, it);
    }

    public abstract void setCallSite() throws BindException;

    public abstract MethodHandle getMethodHandle();

    private static class FieldSelector extends DynamicSelector {
        private final String name;
        private final DynamicMetaFactory.INVOKE_TYPE it;
        private MutableCallSite mc;
        private MethodHandles.Lookup caller;
        private MethodType type;
        private Object[] arguments;
        private MethodHandle handle;

        private FieldSelector(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments, DynamicMetaFactory.INVOKE_TYPE it) {
            this.mc = mc;
            this.caller = caller;
            this.type = type;
            this.name = name;
            this.arguments = arguments;
            this.it = it;
        }

        public void setCallSite() throws BindException {
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
                    Field field = receiver.getClass().getField(name);
                    switch (it) {
                        case GET:
                            handle = caller.unreflectGetter(field);
                            break;
                        case SET:
                            handle = caller.unreflectSetter(field);
                            break;
                    }

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
