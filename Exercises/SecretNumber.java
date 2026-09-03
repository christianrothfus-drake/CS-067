import java.util.Scanner;

public class SecretNumber {
    public static void main(String[] args)
    {
        Scanner keyboard = new Scanner(System.in);

        boolean secretNumberGuessed = false;

        while (secretNumberGuessed == false)
        {
            System.out.print("What do you think the secret integer is? ");
            int userGuess = keyboard.nextInt();

            if (userGuess == 7)
            {
                secretNumberGuessed = true;
            }
            else
            {
                System.out.println("Nope. Try again!\n");
            }
        }

        System.out.println("Congratulations, you guessed the secret number!");

        keyboard.close();
    }    
}
