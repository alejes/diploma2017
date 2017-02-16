package kotlin;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;

public class DynamicCallSite extends MutableCallSite{
    public DynamicCallSite(MethodType type) {
        super(type);
    }

    public DynamicCallSite(MethodHandle target) {
        super(target);
    }

    @Override
    public void setTarget(MethodHandle newTarget) {
        super.setTarget(newTarget);
    }

    @Override
    public MethodType type() {
        return super.type();
    }
}
