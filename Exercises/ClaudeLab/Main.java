package ClaudeLab;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(new Dog("Rex", "dog treats"));
        animals.add(new Cat("Whiskers", "tuna"));

        Scanner scanner = new Scanner(System.in);
        System.out.println("Add animals to the collection (type 'done' to stop).");
        while (true) {
            System.out.print("Type (dog/cat) or 'done': ");
            String type = scanner.nextLine().trim();
            if (type.equalsIgnoreCase("done")) {
                break;
            }

            System.out.print("Name: ");
            String name = scanner.nextLine().trim();

            if (type.equalsIgnoreCase("dog")) {
                animals.add(new Dog(name, "dog treats"));
            } else if (type.equalsIgnoreCase("cat")) {
                animals.add(new Cat(name, "tuna"));
            } else {
                System.out.println("Unknown type, try 'dog' or 'cat'.");
            }
        }
        scanner.close();

        for (Animal animal : animals) {
            animal.makeSound();
            animal.introduceDiet();
            System.out.println();
        }
    }
}
