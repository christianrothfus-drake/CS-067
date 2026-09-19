package Assignment6;

public class Star extends CelestialBody
{
    double mass;

    // This is a Star class. For the sake of this assignment, 
    // a star is a stationary object with some mass and orbiting planets.
    public Star(String name, double mass, double xPosition, double yPosition)
    {
        super(name);

        this.mass = mass;

        setPosition(xPosition, yPosition);
    }
    
    // This function applies a gravitation force to the given body and updates the body's velocity
    public void applyGravity(Planet bodyAttracting)
    {
        // Since the accelerations are in m/day^2, they have the same numeric value as the velocity.
        // This is because (change in velocity = acceleration * time) and time=1 since
        // everything is in terms of days, which is the step size of this program
        double[] velocityDeltas = getAccelerations(bodyAttracting);

        double xVelocityDelta = velocityDeltas[0];
        double yVelocityDelta = velocityDeltas[1];

        bodyAttracting.changeVelocity(xVelocityDelta, yVelocityDelta);
    }

    // Calculates acceleration to apply to a given body in terms of meters per day^2
    private double[] getAccelerations(Planet orbitingBody)
    {
        // Equation: Acceleration = G * m * distance / abs(distance)^3
        // where G is the gravitational constant and m is the mass of the body applying gravity
        // Do this for the x distances and the y distances

        double[] accelerations = new double[2];

        double[] distances = getDistance(orbitingBody);

        double xDistance = distances[0];
        double yDistance = distances[1];
        double euclideanDistance = distances[2];

        // This number is the gravitational constant G converted to be in days rather than seconds.
        // The number is hard coded because it includes a multiplication by 10^-11 which messes with the numbers.
        double gravConst = 0.49823382528;

        double xAcceleration = gravConst * mass * xDistance / Math.pow(euclideanDistance, 3);
        double yAcceleration = gravConst * mass * yDistance / Math.pow(euclideanDistance, 3);

        accelerations[0] = xAcceleration;
        accelerations[1] = yAcceleration;

        return accelerations;
    }

    // This function returns the x distance, y distance, and euclidean distance between the star and the other given body.
    private double[] getDistance(CelestialBody orbitingBody)
    {
        double[] distances = new double[3];

        double xDistance = xPosition - orbitingBody.getXPosition();
        double yDistance = yPosition - orbitingBody.getYPosition();
        double euclideanDistance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
        
        distances[0] = xDistance;
        distances[1] = yDistance;
        distances[2] = euclideanDistance;

        return distances;
    }
}
