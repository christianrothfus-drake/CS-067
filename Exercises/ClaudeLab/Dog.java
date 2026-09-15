package ClaudeLab;
public class Dog extends Animal {
    public Dog(String name, String food)
    {
        super(name, food);
    }

    public void makeSound()
    {
        System.out.println(getName() + " says Woof!");
    }
}
