import java.util.HashMap;
import java.util.Scanner;

public class HashMapTesting
{
    public static void main()
    {
        HashMap<Integer, String> passwords = new HashMap<Integer, String>();

        passwords.put(101, "password123");
        passwords.put(102, "IHeartCS");
        passwords.put(103, "password123");
        System.out.println(passwords);

        System.out.println(passwords.get(102));

        Scanner scanner = new Scanner(System.in);

        System.out.print("Please input a password ID: ");
        int passwordID = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Please input a password: ");
        String password = scanner.next();
        
        if (passwords.containsKey(passwordID))
        {
            if (passwords.get(passwordID).equals(password))
            {
                System.out.println("SUCCESS!");
            }
            else
            {
                System.out.println("FAILURE!");
            }
        }

        passwords.remove(103);
        
        System.out.println(passwords);

        scanner.close();
    }    
}
