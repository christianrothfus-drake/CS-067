
/**
 * Class Exercise on 10/1/2026
 * update the code to catch the InputMismatchException if the user
 * enters something that is not an int
 */
import java.util.InputMismatchException;
import java.util.Scanner;

public class TryCatch {
  public static void main (String[] args){
		int value = -1;
        Scanner keyboard = new Scanner(System.in);
        while (value != 5)
        {
            try
            {
            System.out.println("Please enter the integer 5: ");
            value = keyboard.nextInt();
            keyboard.nextLine(); // clear the input buffer
            }
            catch (InputMismatchException exception)
            {
                System.out.println("please enter an integer");
                keyboard.nextLine();
                value = keyboard.nextInt();
            }
        }
        System.out.println("you did it!");
        keyboard.close();
	}  
}