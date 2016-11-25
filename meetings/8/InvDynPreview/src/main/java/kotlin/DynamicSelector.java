package kotlin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.BindException;
import java.util.*;

public abstract class DynamicSelector {
    protected final String name;
    protected MutableCallSite mc;
    protected MethodHandles.Lookup caller;
    protected MethodType type;
    protected Object[] arguments;
    protected MethodHandle handle;

    private DynamicSelector(Object[] arguments, MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name) {
        this.arguments = arguments;
        this.mc = mc;
        this.caller = caller;
        this.type = type;
        this.name = name;
    }

    public static DynamicSelector getSelector(MutableCallSite mc,
                                              MethodHandles.Lookup caller,
                                              MethodType type,
                                              String name,
                                              Object[] arguments,
                                              DynamicMetaFactory.INVOKE_TYPE it) {
        if (it == DynamicMetaFactory.INVOKE_TYPE.METHOD){
            return new MethodSelector(mc, caller, type, name, arguments);
        }
        else {
            return new FieldSelector(mc, caller, type, name, arguments, it);
        }
    }

    public abstract void setCallSite() throws BindException;

    public MethodHandle getMethodHandle() {
        return handle;
    }

    private static class MethodSelector extends DynamicSelector {
        private MethodSelector(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments) {
            super(arguments, mc, caller, type, name);
        }

        @Override
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
                throw new UnsupportedOperationException("static");
            } else {
                List<Method> methods = new ArrayList<>(Arrays.asList(receiver.getClass().getDeclaredMethods()));
                Collections.addAll(methods, receiver.getClass().getMethods());

                Optional<Method> targetMethod = methods.stream().distinct().filter(it -> it.getName().equals(name)).findFirst();

                if (!targetMethod.isPresent()) {
                    throw new BindException("Runtime: cannot find target method " + name);
                }
                targetMethod.get().setAccessible(true);

                try {
                    handle = caller.unreflect(targetMethod.get());
                } catch (IllegalAccessException e) {
                    throw new BindException(e.getMessage());
                }
            }
        }
    }

    private static class FieldSelector extends DynamicSelector {
        private final DynamicMetaFactory.INVOKE_TYPE it;

        private FieldSelector(MutableCallSite mc, MethodHandles.Lookup caller, MethodType type, String name, Object[] arguments, DynamicMetaFactory.INVOKE_TYPE it) {
            super(arguments, mc, caller, type, name);
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
                throw new UnsupportedOperationException("static");
            } else {
                try {
                    Field field = receiver.getClass().getField(name);
                    switch (it) {
                        case GET:
                            // handle = caller.findGetter(receiver.getClass(), name, ???type)
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
    }
}
