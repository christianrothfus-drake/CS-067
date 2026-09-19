package Assignment6;

public class Planet extends CelestialBody
{
    Star parentBody;

    double orbitingDistance;

    double xVelocity;
    double yVelocity;

    int orbitsCompleted;

    // This is a planet class. It is intended to orbit a star at the given orbiting distance and velocity.
    // It's position is originated at the equivalent to (1, 0) on the unit circle where (0, 0) is the Star it orbits and the radius is the orbiting distance.
    public Planet(String name, Star sun, double orbitingDistance, double orbitingVelocity)
    {
        super(name);

        this.parentBody = sun;

        this.orbitingDistance = orbitingDistance;

        this.xVelocity = 0;
        this.yVelocity = orbitingVelocity;

        this.orbitsCompleted = 0;

        setPosition(sun.xPosition + orbitingDistance, sun.yPosition); // Setting the position as outlined in the comment above
    }

    // This function represents one time step passing for this object
    public void takeStep()
    {
        double old_y = yPosition;

        xPosition += xVelocity;
        yPosition += yVelocity;

        double new_y = yPosition;

        // Since the starting position is the equivalent of (1, 0), an orbit is completed when the planet
        // crosses the x axis from bottom to top again.
        if (old_y < 0 && new_y > 0)
        {
            orbitsCompleted++;

            celebrateNewYear();
        }
    }

    // This function updates the planet's velocities by the given parameters
    public void changeVelocity(double horizontalChange, double verticalChange)
    {
        xVelocity += horizontalChange;
        yVelocity += verticalChange;
    }

    // This function contains the 'decorative' actions to be taken once an orbit has been completed.
    private void celebrateNewYear()
    {
        String numberSuffix;

        switch (orbitsCompleted)
        {
            case 1:
                numberSuffix = "st";
                break;
            case 2:
                numberSuffix = "nd";
                break;
            default:
                numberSuffix = "th";
                break;
        }
        
        System.out.println(name + " completed its " + orbitsCompleted + numberSuffix + " orbit!");
    }

    public String toString()
    {
        return String.format(   name + 
                                "\n     Orbiting: " + parentBody.name +
                                "\n     Orbits Completed: " + orbitsCompleted +
                                "\n     Position: (%,2f, %,2f)" +
                                "\n     Velocity: (%,2f, %,2f)",
                                xPosition, yPosition,
                                xVelocity, yVelocity);
    }
}
