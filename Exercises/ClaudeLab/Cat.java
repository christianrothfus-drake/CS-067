package ClaudeLab;
// .java: this file holds Java source code (human-readable text the compiler reads).
// Compiling it with javac produces Cat.class, the JVM bytecode the "java" command runs.

public class Cat extends Animal {
    public Cat(String name, String food)
    {
        super(name, food);
    }

    public void makeSound()
    {
        System.out.println(getName() + " says Meow!");
    }
}
