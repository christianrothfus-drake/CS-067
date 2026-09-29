package Assignment7;

public class DProfessor extends Professor{
	public DProfessor(String n, int a, String s)
	{
		super(n, a, s);
	}
	
	public String brag()
	{
		return "I'm distinguished!";
	}
	
	public String toString()
	{
		String returnString = "";
		returnString += "Distinguished Professor: ";
		returnString += " name: " + getName() + "\n";
		returnString += " age: " + getAge()+ "\n";
		returnString += " specialization: " + getSpecialization()+ "\n";
		returnString += "Did I mention that " + brag() + "\n";
		return returnString;
	}
}
