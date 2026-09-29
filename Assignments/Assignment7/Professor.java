package Assignment7;

public class Professor extends Person{
	private String specialization;
	
	public Professor(String n, int a, String s)
	{
		super(n, a);
		specialization = s;
	}
	
	public String getSpecialization()
	{
		return specialization;
	}
	
	public void setSpecialization(String s)
	{
		specialization = s;
	}
	
	public String toString()
	{
		String returnString = "";
		returnString += "Professor: " + "\n";
		returnString += " name: " + name +  "\n";
		returnString += " age: " + age + "\n";
		returnString += " specialization: " + specialization + "\n";
		return returnString;
	}
}
