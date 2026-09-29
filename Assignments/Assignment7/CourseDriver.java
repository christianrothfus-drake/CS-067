package Assignment7;

public class CourseDriver
{
    public static void main()
    {
        Professor urness = new Professor("Urness", 30, "Computer Science");
        TeachingAssistant angel = new TeachingAssistant("Angel", 10_000, 3.5);

        Professor tong = new Professor("Tong", 50, "Actuarial Science");
        TeachingAssistant sophie = new TeachingAssistant("Sophie", 22, 4.0);

        Professor andrei = new Professor("Andrei Migunov", 30, "Computer Science");
        TeachingAssistant bethany = new TeachingAssistant("Bethany", 20_000, 2.4);

        Student[] students = new Student[4];
        students[0] = new Student("Christian", 21, 3.5, false);
        students[1] = angel;
        students[2] = sophie;
        students[3] = bethany;

        Course[] courses = new Course[4];
        courses[0] = new Course("CS 067", urness, angel, students);
        courses[1] = new Course("ACTS 135", tong, sophie, students);
        courses[2] = new Course("CS 087", andrei, bethany, students);
        courses[3] = new Course("CS 167", urness, angel, students);

        angel.setCourseTutoring(courses[0], courses[3]);
        sophie.setCourseTutoring(courses[1]);
        bethany.setCourseTutoring(courses[2]);

        for (Student student : students)
        {
            System.out.println(student + "\n");
        }

        // This is the main loop for the function.
        // The students will take 1 exam for each course they are part of.
        //      Before each exam they will have 31 days to study.
        //          Each student will choose to study approximately 10% of the time

        for (Course course : courses)
        {
            for (int day=1; day <= 31; day++)
            {
                for (Student student : students)
                {
                    // The teaching assistant may sit in the course, but does not study
                    if (course.getTeachingAssistant().equals(student))
                    {
                        continue;
                    }

                    double randomNum = Math.random();

                    if (randomNum > student.calculateStudyLikelihood())
                    {
                        continue;
                    }
                    
                    if (student.studiesSolo == false)
                    {
                        course.getTeachingAssistant().giveHelp(student);
                    }
                    else
                    {
                        student.study();
                    }
                }
            }

            System.out.println("Time for the " + course.getCourseName() + " final exam!");
            course.holdExam();
            System.out.println();
        }

        for (Student student : students)
        {
            System.out.println(student.toString(false));
        }
    }
}