import org.jetbrains.annotations.Nullable;

public class DynamicBox {
    private final Object storage;

    public DynamicBox(@Nullable Object obj) {
        storage = obj;
    }

    @Nullable
    public Object unbox() {
        return storage;
    }
}
