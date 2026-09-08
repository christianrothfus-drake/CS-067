import java.util.Random;
import java.io.*;

/**
 * WritingRandom
 */
public class WritingRandom
{   
    public static void main(String[] args) throws IOException
    {
        Random generator = new Random();
        PrintWriter outputFile = new PrintWriter("RandomOutput.txt");

        int randomInteger;

        for (int i=1; i<=5; i++)
        {
        randomInteger = generator.nextInt(1, 11);

        outputFile.println(randomInteger);
        }

        outputFile.close();

    }
}