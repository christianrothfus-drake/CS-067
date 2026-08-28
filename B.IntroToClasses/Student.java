public class Student
{
    String name;
    int age;
    
    public Student(String name, int age)
    {
        this.name = name;
        this.age = age;
    }

    public void introduce()
    {
        System.out.println(
        "Hi, my name is " + name + " and I am " + age + " years old");
    }
}
