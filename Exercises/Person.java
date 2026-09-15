public class Person {
    String name;
    Date birthday;
    
    public Person(String name, Date birthday)
    {
        this.name = name;
        this.birthday = birthday;
    }

    public void introduce()
    {
        System.out.println("Hello, I am " + name + " and my birthday is " + birthday + "!");
    }
}
