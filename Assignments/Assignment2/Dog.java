package Assignment2;
import java.util.Scanner;

public class Dog
{
    Scanner scanner;
    String name;
    String owner;
    String breed;
    int age;

    public Dog(Scanner scanner)
    {
        this.scanner = scanner;

        this.name = getName(scanner);
        this.owner = getOwner(scanner);
        this.breed = getBreed(scanner);
        this.age = getAge(scanner);

    }

    public void introduce(){
        System.out.println("\nHello, I am " + name + " and my owner is " + owner + ".");
        System.out.println("I am a " + breed + " and I am " + age + " years old!\n");
    }

    public String getName(Scanner scanner)
    {
        System.out.print("What is the dog's name? ");
        String givenName = scanner.nextLine();

        return givenName;
    }

    public String getOwner(Scanner scanner)
    {
        System.out.print("What is the owner's name? ");
        String givenOwner = scanner.nextLine();

        return givenOwner;
    }

    public String getBreed(Scanner scanner)
    {
        System.out.print("What is the dog's breed? ");
        String givenBreed = scanner.nextLine();

        return givenBreed;
    }

    public int getAge(Scanner scanner)
    {
        System.out.print("How old is the dog? ");
        int givenAge = scanner.nextInt();

        return givenAge;
    }
}
