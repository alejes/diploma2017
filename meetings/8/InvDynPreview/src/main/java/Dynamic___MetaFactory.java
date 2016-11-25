import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.BindException;
import java.util.*;

/**
 * Created by alejes on 19.11.16.
 */
public class Dynamic___MetaFactory {
    private static MethodHandle hw;

    public static void hw() {
        System.out.println("Hello, World!");
    }

    public static void foo() {
        System.out.println("Hello, foo");
    }

    public static void foo(int x) {
        System.out.println("Hello, foo");
    }

    public static CallSite bootstrapDynamic(MethodHandles.Lookup caller,
                                            String name,
                                            MethodType type)
            throws IllegalAccessException, NoSuchMethodException, BindException {
        List<Method> methods = new ArrayList<>(Arrays.asList(caller.lookupClass().getDeclaredMethods()));
        Collections.addAll(methods, caller.lookupClass().getMethods());

        Optional<Method> targetMethod = methods.stream().distinct().filter(it -> it.getName().equals(name)).findFirst();

        if (!targetMethod.isPresent()) {
            throw new BindException("Runtime: cannot find target method " + name);
        }
        targetMethod.get().setAccessible(true);

        hw = caller.unreflect(targetMethod.get());

        return new ConstantCallSite(hw);
    }


    public static CallSite bootstrapDynamicFieldGetter(MethodHandles.Lookup caller,
                                                       String name,
                                                       MethodType type)
            throws IllegalAccessException, NoSuchMethodException, BindException {
        if (type.returnType().equals(Object.class)) {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            List<Field> fields = new ArrayList<>(Arrays.asList(caller.lookupClass().getDeclaredFields()));
            Collections.addAll(fields, caller.lookupClass().getFields());
            Optional<Field> targetField = fields.stream().distinct().filter(it -> it.getName().equals(name)).findFirst();
            if (!targetField.isPresent()) {
                throw new BindException("Runtime: cannot find target method getter " + name);
            }
            targetField.get().setAccessible(true);

            hw = caller.unreflectGetter(targetField.get());
        } else {
            try {
                hw = caller.findGetter(caller.lookupClass(), name, type.returnType());
            } catch (NoSuchFieldException e) {
                //[TODO] static getter
                throw new BindException("Runtime: cannot find target field " + name);
            }
        }


        return new ConstantCallSite(hw);
    }
}
