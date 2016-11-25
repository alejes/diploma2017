/**
 * Created by Alexey on 26.11.2016.
 */
public class TestsInner {
    protected int s = 4;
    static innerCl get(){
        return new innerCl();
    }
    private static class innerCl extends TestsInner {
        int result(){
            return s;
        }
    }

    public static void main(String[] args) {
        System.out.println(get().result());
    }
}
