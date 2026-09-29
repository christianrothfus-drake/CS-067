Christian Rothfus

1. Reading
The article helped with my understanding of interfacing with classes.
Before I didn't really have a picture in my head of how it worked, but I believe I do now, though I am not positive it is accurate.

2. What I changed
The main change was the addition of the TeachingAssistant class, as well as the Course class.
These were added to create a little simulation of a semester going by and students taking classes.
The teaching assistant is a student, except they do not take the class they tutor in,
and they always study alone (never with another tutor). The course class sort of runs the main
portion of the simulation which is the holding of exams. I also modified the student
class to support a more complex GPA system (the average of your recorded class results (exam scores)),
and an option to study with the assistant which is more productive than studying alone.

3. Overloading/overriding
In the teaching assistant class I overrode the toString method to introduce the student and what they are tutoring.
In the teaching assistant class I also overloaded the function that sets which class they are assisting in, to allow
for a seperate call to have them assist in two classes instead.
In the student class I overloaded the toString method to have another call that takes a boolean.
If it is true, it displays alternate information for the student. If false, it introduces the student, how many classes they've taken, and their GPA as a result.

4. Challenges
I was happy with where I got to before really implementing any overloading or overriding, so it was a challenge to think of how to add more.

5. AI
I did not use any AI.