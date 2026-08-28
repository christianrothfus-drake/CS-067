public class HelloWorld
{
    public static void main(String[] args)
    {
        for (int row = 1; row <= 5; row++)
            {
            int prior_numbers = triangle_sum(row - 1);
            for (int col = 1; col <= (5 - row + 1); col++)
            {
                System.out.print((prior_numbers + col) + " ");
            }
            System.out.println();
        }
    }

    public static int triangle_sum(int total_rows)
    {
        int current_sum = 0;

        for (int row = 1; row <= total_rows; row++)
            {
            current_sum += (5 - row + 1);
            }
        
        return current_sum;
    }
    
}