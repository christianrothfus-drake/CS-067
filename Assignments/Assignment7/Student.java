package Assignment7;
import java.util.ArrayList;

public class Student extends Person{
	private double gpa;
	protected int effectiveStudyHours;
	protected boolean studiesSolo;
	protected ArrayList<Double> gradeHistory = new ArrayList<Double>();
	
	public Student(String n, int a, double g, boolean studiesSolo)
	{
		super(n, a);
		this.gpa = g;
		this.studiesSolo = studiesSolo;
		this.effectiveStudyHours = 0;
	}
	
	public void setGPA(double g)
	{
		gpa = g;
	}

	public void setGPA(int g)
	{
		gpa = g * 1.0;
	}
	
	public double getGPA()
	{
		return gpa;
	}
	
	// Added a function to allow for the getting of the effective study hours attribute.
	public int getStudyHours()
	{
		return effectiveStudyHours;
	}

	// Added a function allow for resetting the effective study hours (after an exam)
	public void resetStudyHours()
	{
		effectiveStudyHours = 0;
	}

	public void study()
	{
		effectiveStudyHours++;
	}
	
	// This function represents studying a teaching assistant.
	// Studying with a teaching assistant is assumed to be more effective than studying normally.
	// One session of studying with an assistant increases the effective study hours by the assistant's GPA
	// rounded down to the nearest integer.
	public void studyWithAssistant(int effectiveHours)
	{
		effectiveStudyHours += effectiveHours;
	}

	// This function records a new grade (0-4) and puts it in the students records of grades.
	public void recordGrade(Double newGrade)
	{
		gradeHistory.add(newGrade);
	}

	// This function calculates the student's cumulative GPA, updates the gpa attribute, and returns it to the user.
	public double calculateGPA()
	{
		int totalClasses = gradeHistory.size();
		double gpaSum = 0.0;

		for (Double grade : gradeHistory)
		{
			gpaSum += grade;
		}

		gpa = gpaSum / totalClasses;

		return gpa;
	}

	// This function calculates a student's likelihood of studying.
	// If the GPA has fallen below a threshold (3.5) then the student is more likely to study.
	public double calculateStudyLikelihood()
	{
		// If the 
		if (gradeHistory.size() > 0)
		{
			calculateGPA();
		}

		// If the student has a GPA lower than 3.5, they study 25% of the time.
		if (getGPA() < 3.5)
		{
			return 0.25;
		}
		// If the student has a GPA of at least 3.5, they study 10% of the time.
		else
		{
			return 0.10;
		}
	}

	public String toString()
	{
		String returnString = "";
		returnString += "Student: " + "\n";
		returnString += " name: " + name + "\n";
		returnString += " age: " + age + "\n";
		returnString += " gpa: " + gpa + "\n";
		return returnString;
	}

	// Overloading the toString method so the user can choose to request the study habits or an introduction of the  
	public String toString(boolean requestStudyHabits)
	{
		if (requestStudyHabits == true)
		{
			return 	"Student: \n" +
					"\t Classes Taken: " + gradeHistory.size() +
					"\t GPA: " + gpa +
					"\t Study Likelihood: " + calculateStudyLikelihood();
		}

		else
		{
			return 	"My name is " + name + " and after taking " + gradeHistory.size() + " classes I have a GPA of " + gpa + "!";
		}
	}
}
