package Assignment7;

public class Person {
	protected String name;
	protected int age;
	
	public Person(String n, int a)
	{
		name = n;
		age = a;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public void setAge(int a)
	{
		age = a;
	}
	
	public int getAge()
	{
		return age;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		String returnString = "";
		returnString += "PERSON:\n";
		returnString += " name: " + name + "\n";
		returnString += " age: " + age+ "\n";
		return returnString;
	}
}
