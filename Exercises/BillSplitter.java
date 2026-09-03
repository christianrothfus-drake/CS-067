import java.util.Scanner;

public class BillSplitter
{
    public static void main(String[] args)
    {
        Scanner keyboard = new Scanner(System.in);

        int totalCustomers = getCustomers(keyboard);
        double grossCost = getCost(keyboard);

        keyboard.close();

        double netCost = grossCost * 1.15;

        double individualCost = netCost / totalCustomers;

        System.out.println("Each person should pay $" + individualCost);
    }

    public static int getCustomers(Scanner scanner)
    {
        boolean validInput = false;
        int userInput = 0;

        while (validInput == false)
        {
            System.out.println("How many customers were there? ");
            userInput = scanner.nextInt();

            if (userInput > 0)
            {
                validInput = true;
            }
        }

        return userInput;
    }

    public static double getCost(Scanner scanner)
    {
        boolean validInput = false;
        double userInput = 0;
        
        while (validInput == false)
        {
            System.out.println("What was the total bill? ");
            userInput = scanner.nextDouble();

            if (userInput > 0)
            {
                validInput = true;
            }
        }

        return userInput;
    }
}
