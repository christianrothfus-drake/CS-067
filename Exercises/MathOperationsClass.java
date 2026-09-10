public class MathOperationsClass {
    public static void main(String[] args)
    {
        double arg1 = Double.valueOf(args[0]);
        double arg2 = Double.valueOf(args[1]);

        System.out.println("Addition: " + (arg1 + arg2));
        System.out.println("Subtraction: " + (arg1 - arg2));
        System.out.println("Multiplication: " + (arg1 * arg2));
        System.out.println("Division: " + (arg1 / arg2));
    }


}
