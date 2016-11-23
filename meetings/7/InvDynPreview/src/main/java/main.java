import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.LambdaMetafactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;

/**
 * Created by alejes on 19.11.16.
 */
public class main {
    public int method(){
        return 5;
    }
    public static void main(String[] args) throws Throwable {
        String name = "method";
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class thisClass = lookup.lookupClass();
        List<Method> methodList = Arrays.stream(thisClass.getMethods()).filter(it -> it.getName().equals(name)).collect(Collectors.toList());

        Object i = 4;

/*
        Integer z = 2;
        z.intValue();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mh = lookup.findVirtual(i.getClass(), "intValue", methodType(int.class));
        //lookup.findSpecial()

        System.out.println(mh.invoke(i));


        MethodHandle MH_concat = publicLookup().findVirtual(String.class,
                "concat", methodType(String.class, String.class));


*/
    }
}
