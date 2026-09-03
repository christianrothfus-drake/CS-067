public class Looping {
    public static void main(String[] args)
    {
        for (int i=1; i<=5; i++)
        {
            System.out.println(i);
        }

        for (int i=33; i<=66; i=i+3)
        {
            System.out.println(i);
        }

        int current_sum = 0;
        for (int i=1; i<=99; i++)
        {
            current_sum += i;
        }
        System.out.println("The sum of 1-99 is " + current_sum);

        for (int i=100; i>=0; i--)
        {
            System.out.println(i);
        }
    }    
}
