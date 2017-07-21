/**
 * Created by Gabriela on 19-Jul-17.
 */
public class variableTriples {
    private final int a;
    private final int b;
    private final int c;

    public variableTriples (int a, int b, int c){
        this.a = a;
        this.b = b;
        this.c = c;
    }


    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }
    public void printTriples(){
        System.out.print(a);
        System.out.print(" ");
        System.out.print(b);
        System.out.print(" ");
        System.out.println(c);

    }

}
