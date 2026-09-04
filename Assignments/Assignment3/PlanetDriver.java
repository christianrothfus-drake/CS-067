// Christian Rothfus
package Assignment3;

// Acknowledgements
//    Earth Wiki - Source for how fast the Earth travels around the sun
//        https://en.wikipedia.org/wiki/Earth#Orbit_and_location

public class PlanetDriver {
    public static void main(String[] args)
    {
        String planetName = "Earth";
        double orbitDistance = 93_000_000; // Approximate distance from the sun in millions of miles
        double orbitSpeed = 66_622; // Approximate number of miles Earth travels in 1 hour

        Planet myPlanet = new Planet(planetName, orbitDistance, orbitSpeed);

        myPlanet.introduce();

        myPlanet.orbit();
    }
}
