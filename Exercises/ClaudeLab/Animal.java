package ClaudeLab;
public abstract class Animal {
    private String name;
    private String favoriteFood;

    public Animal(String name, String food)
    {
        this.name = name;
        this.favoriteFood = food;
    }

    public String getName()
    {
        return name;
    }

    public void introduceDiet()
    {
        System.out.println(name + " really likes to eat " + favoriteFood + "!");
    }

    public abstract void makeSound();
}
