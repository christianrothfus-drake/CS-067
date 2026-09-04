// Christian Rothfus
package Assignment3;
import java.lang.Math;

public class Planet
{
    String name;
    double orbitDistance;
    double orbitCircumference;
    double orbitSpeed;
    double orbitProgress;

    public Planet(String name, double orbitDistance, double orbitSpeed)
    {
        this.name = name;
        this.orbitDistance = orbitDistance;
        this.orbitCircumference = Math.PI * orbitDistance * 2;
        this.orbitSpeed = orbitSpeed;
        this.orbitProgress = 0;
    }

    public void introduce()
    {
        System.out.println("Hello, I am " + name + "!");
        System.out.println("I am orbiting at a distance of " + orbitDistance);
        System.out.println("I am orbiting at a speed of " + orbitSpeed);

        double orbitTimeSteps = orbitCircumference / orbitSpeed;

        System.out.println("Consequently, it takes " + orbitTimeSteps + " time steps to complete my orbit.");
    }

    public void orbit()
    {
        int orbitsCompleted = 0;

        for (int timeStep=1; timeStep<=1_000_000; timeStep++)
        {
            orbitProgress = orbitProgress + (orbitSpeed / orbitCircumference);

            if (orbitProgress > 1)
            {
                orbitsCompleted++;
                
                System.out.println("I completed orbit " + orbitsCompleted + " on time step " + timeStep);

                orbitProgress = orbitProgress - 1;
            }
        }
    }
}