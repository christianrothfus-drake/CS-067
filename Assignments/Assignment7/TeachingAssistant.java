package Assignment7;

public class TeachingAssistant extends Student {
    Course courseTutoring;
    Course[] coursesTutoring;

    boolean tutorsMany;

    public TeachingAssistant(String name, int age, double gpa)
    {
        super(name, age, gpa, true);
    }

    // This function represents a student studying with the teaching assistant.
	// Studying with a teaching assistant is assumed to be more effective than studying normally.
	// One session of studying with an assistant increases the effective study hours by the assistant's GPA
	// rounded down to the nearest integer.
    public void giveHelp(Student tutoree)
    {
        int tutoringEffectiveness = (int) getGPA();

        tutoree.studyWithAssistant(tutoringEffectiveness);
    }

    public void setCourseTutoring(Course courseTutoring)
    {
        this.courseTutoring = courseTutoring;
        tutorsMany = false;
    }

    // Overloading the setCourseTutoring to allow for tutoring multiple courses.
    public void setCourseTutoring(Course course1, Course course2)
    {
        this.coursesTutoring = new Course[2];
        coursesTutoring[0] = course1;
        coursesTutoring[1] = course2;
        tutorsMany = true;
    }

    // returns the courses the student is tutoring as a string. Set up to support having any number of courses, though only 2 or less is possible.
    public String getCoursesTutoring()
    {
        if (tutorsMany == true)
        {
            String coursesString = "";

            for (Course course : coursesTutoring)
            {
                if (course.equals(coursesTutoring[coursesTutoring.length - 1]))
                {
                    coursesString += " and ";
                }

                coursesString += course.getCourseName();

                if (!course.equals(coursesTutoring[coursesTutoring.length - 1]) && coursesTutoring.length > 2)
                {
                    coursesString += ",";
                }
            }

            return coursesString;
        }
        else
        {
            return courseTutoring.getCourseName();
        }
    }

    @Override
    public String toString()
    {
        return "I am " + name + " and I am the assistant for " + getCoursesTutoring() + ".\n" +
        "I have a " + getGPA() + " GPA, so it is " + (int) getGPA() + " times more effective to study with me.";
    }
}
