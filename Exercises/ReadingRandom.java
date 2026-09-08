import java.io.*;
import java.util.Scanner;

public class ReadingRandom {
    public static void main(String[] args) throws IOException
    {
        double current_sum = 0;
        double current_count = 0;
        int current_int;

        File fileReadingFrom = new File("RandomOutput.txt");

        Scanner reader = new Scanner(fileReadingFrom);

        while (reader.hasNext())
        {
            current_int = reader.nextInt();

            current_sum += current_int;
            current_count++;
        }

        reader.close();

        System.out.println(current_sum / current_count);
    }
}
