import java.util.Scanner;

public class FoodPrompter
{
    public static void main(String[] args)
    {
        Scanner keyboard = new Scanner(System.in);

        System.out.print("What is the temperature in Fahrenheit right now? ");
        int userInput = keyboard.nextInt();

        if (userInput > 90)
        {
            System.out.println("time for ice cream");
        }
        else
        {
            System.out.println("time for cake");
        }

        keyboard.close();
    }
}
