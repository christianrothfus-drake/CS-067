import java.util.HashSet;
import java.util.ArrayList;

public class HashTesting {
    public static void main()
    {
        ArrayList<Integer> myIntsList = new ArrayList<Integer>();
        HashSet<Integer> myIntsSet = new HashSet<Integer>();

        for (int i=0; i<100_000; i++)
        {
            Integer newRandomInt = (int) (Math.random() * 100_000 + 1);

            myIntsList.add(newRandomInt);
            myIntsSet.add(newRandomInt);
        }

        long startTime;
        long endTime;

        startTime = System.nanoTime();
        myIntsList.contains(57);
        endTime = System.nanoTime();

        System.out.println("It took " + (endTime - startTime) + " nanoseconds to search the ArrayList.");

        startTime = System.nanoTime();
        myIntsSet.contains(57);
        endTime = System.nanoTime();

        System.out.println("It took " + (endTime - startTime) + " nanoseconds to search the HashSet.");
    }
}
