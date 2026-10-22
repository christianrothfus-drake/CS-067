/**
 * The RocketShip class represents a rocket ship that can be launched into space.
 */
public class RocketShip {

    private String name;
    private int fuel;
    private int speed;
    private int altitude;

    /**
     * Constructs a new RocketShip object with the given name, fuel, speed, and altitude.
     */
    public RocketShip(String name, int fuel, int speed, int altitude) {
        this.name = name;
        this.fuel = fuel;
        this.speed = speed;
        this.altitude = altitude;
    }

    /**
     * Launches the rocket ship into space.
     */
    public void launch() {
        System.out.println("3... 2... 1... BLAST OFF!");
    }

    /**
     * Returns a string representation of the rocket ship.
     */
    @Override
    public String toString() {    
        return "Name: " + name + " Fuel: " + fuel + " " + "Speed: " + speed + " Alt: " + altitude;
    }
}