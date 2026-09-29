package Assignment7;

public class Course {
    String courseName;
    Professor instructor;
    TeachingAssistant teachingAssistant;
    Student[] students;

    public Course(String courseName, Professor instructor, TeachingAssistant teachingAssistant, Student[] students)
    {
        this.courseName = courseName;
        this.instructor = instructor;
        this.teachingAssistant = teachingAssistant;
        this.students = students;
    }

    public void holdExam()
    {
        for (Student student : students)
        {
            // This stops the teaching assistant from taking the exam.
            if (student.equals(teachingAssistant))
            {
                continue;
            }

            double studentGPA = student.getGPA();
            double randomAdjustment = (Math.random() - 0.5) / 10.0; // Including a random adjustment of up to 5% in either direction

            double predictedScore = studentGPA / 4.0 + randomAdjustment;

            predictedScore = Math.min(predictedScore, 0.80); // Capping the predicted score at 80% so students have to study.


            int studyHours = student.getStudyHours();
            double studyEfficiency = Math.max(Math.random(), 0.3); // This sets the minimum efficiency to 30%

            // It is assumed that you can up to earn an additional 3% on an exam if you study for an hour
            double scoreModifier = studyHours * 0.03 * studyEfficiency;
            

            double finalScore = predictedScore + scoreModifier;
            if (finalScore > 1.0)
            {
                finalScore = 1.0;
            }

            System.out.println(student.getName() + " effectively studied for " + student.getStudyHours() + " hours and scored a " + String.format("%.2f", finalScore * 100.0) + "% in " + courseName + "!");

            Double finalGrade;

            if (finalScore >= 0.9)
            {
                finalGrade = 4.0;
            }
            else if (finalScore > 0.8)
            {
                finalGrade = 3.0;
            }
            else if (finalScore > 0.7)
            {
                finalGrade = 2.0;
            }
            else if (finalScore > 0.6)
            {
                finalGrade = 1.0;
            }
            else
            {
                finalGrade = 0.0;
            }

            student.recordGrade(finalGrade);
            student.calculateGPA();

            student.resetStudyHours();
        }
    }

    // Returns the courses teaching assistant
    public TeachingAssistant getTeachingAssistant()
    {
        return teachingAssistant;
    }

    // Returns the courses
    public String getCourseName()
    {
        return courseName;
    }
}
