import java.util.ArrayList;
import java.util.Collections;

public class CollectionsTesting {
    public static void main()
    {
        ArrayList<String> stringList = new ArrayList<String>();
        ArrayList<Integer> intList = new ArrayList<Integer>();    
        ArrayList<RocketShip> rocketList = new ArrayList<RocketShip>();

        stringList.add("Hello");
        stringList.add("Hey, Girl");
        stringList.add("Alphabetical");

        for (int i=0; i<10; i++)
        {
            Integer newInt = (int) (Math.random() * 10 + 1);
            intList.add(newInt);
        }

        rocketList.add(new RocketShip("USS Enterprise", 100, 1000, 100));
        rocketList.add(new RocketShip("Death Star", 1000, 100, 1000));
        rocketList.add(new RocketShip("Toy Rocket", 1, 1, 1));

        Collections.sort(stringList);
        System.out.println(stringList);

        Collections.sort(intList);
        System.out.println(intList);

        // Collections.sort(rocketList);
        System.out.println(rocketList);
    }
}
