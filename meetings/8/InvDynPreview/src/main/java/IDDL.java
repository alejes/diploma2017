/**
 * Created by alejes on 19.11.16.
 */
public class IDDL {
    public static void doWork(Object obj){
        System.out.println(obj.hashCode());
    }

    public static void main(String[] args) {
        Object inst = new TargetClass();
        doWork(inst);
    }
}
