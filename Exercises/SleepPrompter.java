import java.util.Scanner;

public class SleepPrompter
{
    public static void main(String[] args)
    {
        Scanner keyboard = new Scanner(System.in);

        System.out.print("How many hours of sleep did you get last night? ");
        int userInput = keyboard.nextInt();

        if (userInput > 8)
        {
            System.out.println("You are well rested!");
        }
        else if ((4 < userInput) && (userInput <= 8))
        {
            System.out.println("The coffee shop is around the corner.");
        }
        else if ((0 < userInput) && (userInput <=4))
        {
            System.out.println("Are you sure you are awake?");
        }
        else if (userInput <= 0)
        {
            System.out.println("Input error.");
        }

        keyboard.close();
    }
}