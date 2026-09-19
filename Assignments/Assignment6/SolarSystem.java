package Assignment6;

// Acknowledgements
//      https://www.janbasktraining.com/community/java/does-java-have-an-exponential-operator - How to take powers of stuff
//      Wikipedia - All of the numbers and stuff for earth
//      Nasa - Numbers for Mars and Jupiter
//          https://nssdc.gsfc.nasa.gov/planetary/factsheet/marsfact.html
//          https://nssdc.gsfc.nasa.gov/planetary/factsheet/jupiterfact.html

// This file simulates the orbit of our Earth (type Planet) around our sun (type Star).
// The simulation is set up to do calculations based on numbers in terms of kilograms and meters, with each time step being 1 day.

// CelestialBody Class - Represents a generic celestial body. Extended by Star and Planet
// Star Class - Represents a stationary object that applies forces to given Planets.
// Planet Class - Represents an object that moves and can have forces applied to it.

public class SolarSystem
{
    public void main()
    {
        double sunMass = 1.989 * Math.pow(10, 30); // The sun's mass in kg

        double earthOrbitingDistance = 149_597_870_700.0; // Earth's orbiting distance in meters
        double earthVelocity = 2_572_992_000.0; // Earth's velocity in meters per day

        double marsObitingDistance = 227_939_200_000.0;
        double marsVelocity = 2_080_512_000.0;

        double jupiterOrbitingDistance = 778_479_000_000.0;
        double jupiterVelocity = 1_128_384_000.0;

        Star sun = new Star("Sun", sunMass, 0, 0);
        Planet earth = new Planet("Earth", sun, earthOrbitingDistance, earthVelocity);
        Planet mars = new Planet("Mars", sun, marsObitingDistance, marsVelocity);
        Planet jupiter = new Planet("Jupiter", sun, jupiterOrbitingDistance, jupiterVelocity);

        Planet[] planets = new Planet[3];

        planets[0] = earth;
        planets[1] = mars;
        planets[2] = jupiter;

        int timeSteps = 10_000; // The number of steps to simulate. Equivalent to about 27 years

        for (int time_step=1; time_step <= timeSteps; time_step++)
        {
            for (Planet planet : planets)
            {
                planet.takeStep(); // The planet takes a step based on it's current velocity and it's position is updated.

                sun.applyGravity(planet); // The sun applies a gravitational force to the planet and updates the its velocity.
            }
        }

        for (Planet planet : planets)
        {
            System.out.println("\n" + planet);
        }
    }
}
