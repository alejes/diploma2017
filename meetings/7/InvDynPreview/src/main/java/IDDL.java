/**
 * Created by alejes on 19.11.16.
 */
public class IDDL {
    public int myField = 5;
    public String myStringField = "Privet";

    /*public void myMethod(){
        System.out.println("inside iddl");
    }
    public void myMethod(IDDL obj){
        System.out.println("inside iddl");
    }

    private void call(){
        myMethod();
    }*/

    public Object omethod(){
        return  new Object();
    }

    public static void main(String[] args) {
        IDDL inst = new IDDL();
        int z = inst.myField;
        Object v = inst.myStringField;
        System.out.println(z);
        System.out.println(v);
    }
}
