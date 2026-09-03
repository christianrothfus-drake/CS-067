public class CountingBy2 {
    public static void main(String[] args)
    {
        int currentNumber = 2;

        while (currentNumber <= 1024)
        {
            System.out.println(currentNumber);
            
            currentNumber += 2;
        }
    }    
}
