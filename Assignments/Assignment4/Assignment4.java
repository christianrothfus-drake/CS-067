// Christian Rothfus
// Description:
// Secret Message #1: EatMoreVegetables
// Secret Message #2: FourCharsAfterW
// Secret Message #3: tinyurl.com/3s847myv
package Assignment4;
import java.io.*;
import java.util.Scanner;

public class Assignment4
{
    public static void main(String[] args) throws IOException
    {
        File encryptedFile = new File("Assignment4Input.txt");
        Scanner reader = new Scanner(encryptedFile);

        String compiledMessage = compileMessage(reader, encryptedFile);

        // Prints 'EatMoreVegetables'
        String message1 = decodeMessage(compiledMessage, 'X', 3);
        System.out.println(message1);
        
        // Prints FourCharsAfterW
        String message2 = decodeMessage(compiledMessage, 'Y', 5);
        System.out.println(message2);

        // Prints a link to shark week video on the goblin shark that has two jaws :)
        String message3 = decodeMessage(compiledMessage, 'W', 4);
        System.out.println(message3);

        reader.close();
    }

    // I am unsure if the characters being on different lines would mess with the
    // reading characters from strings, so I decided to put them all together into one string.
    // I am assuming that newline characters don't mess up my program (e.g. get counted as a character
    // and subsequently cause a character to be returned early e.g.  XA\nBC --> B instead of C or something)
    public static String compileMessage(Scanner reader, File encyptedFile)
    {
        String message = "";
        
        while (reader.hasNext() == true)
        {
            String nextToken = reader.next();
            message += nextToken;
        }

        return message;
    }

    public static String decodeMessage(String message, char charFinding, int positionLag)
    {
        int messageLength = message.length();

        String secretMessage = "";

        for (int idx=0; idx<messageLength; idx++)
        {
            if (message.charAt(idx) == charFinding)
            {
                char currentLetter = message.charAt(idx + positionLag);
                String letterAdding = Character.toString(currentLetter);

                secretMessage += letterAdding;
            }
        }

        return secretMessage;
    }
}