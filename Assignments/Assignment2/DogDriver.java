package Assignment2;
import java.util.Scanner;

public class DogDriver {
    public static void main(String[] args)
    {
        Scanner keyboard = new Scanner(System.in);

        Dog dog1 = new Dog(keyboard);
        dog1.introduce();

        keyboard.nextLine();

        Dog dog2 = new Dog(keyboard);
        dog2.introduce();

        keyboard.close();
    }
}
