// Represents a player-controlled character.
// Players can attack, defend, heal, and upgrade equipment.

package Assignment5.JavaCaveGame;

public class Player extends Character {

    // Used to assign unique IDs to players
    private static int NEXT_PLAYER_ID = 1;

    // Player-specific identifier
    private final int id;

    // Number of healing potions the player has
    private int potions;

    // Damage dealt when attacking
    // NEW ATTRIBUTE AND UPDATED ATTRIBUTE -- I changed weaponDamage to targetDamage and added the new spreadDamage attribute
    // targetDamage refers to the value used when attacking a single enemy, while spreadDamage refers to the value used
    // when attacking all enemies
    private int targetDamage;
    private int spreadDamage;

    // Creates a new player with starting stats
    public Player(int hp) {
        super(hp);
        this.id = NEXT_PLAYER_ID++;
        this.potions = 2;
        this.targetDamage = 4; // Starter weapon damage
        this.spreadDamage = 2;
    }

    // Returns the damage dealt by an attack
    // UPDATE -- Added an if statement so it returned the correct value based on the attack type
    public int attack(int attackType) {
        if (attackType == 1)
            {
            return targetDamage;
            }
        else
        {
            return spreadDamage;
        }
        
    }

    // Uses a healing potion to restore random HP
    public void usePotion() {

        // Prevent potion use if none remain
        if (potions <= 0) {
            System.out.println("Player " + id + " has no potions left!");
            return;
        }

        // Heal between 1 and 6 HP
        int heal = (int)(Math.random() * 6) + 1;
        hp += heal;
        potions--;

        System.out.println("Player " + id + " heals " + heal + " HP!");
    }

    // Getter for potion count
    public int getPotions() {
        return potions;
    }

    // Heals a fixed amount (used by rewards and round recovery)
    public void heal(int amount) {
        hp += amount;
    }

    // Adds a healing potion to the inventory
    public void addPotion() {
        potions++;
    }

    // Increases weapon damage (used for upgrades)
    public void upgradeWeapon(int amount) {
        targetDamage += amount;
        spreadDamage += amount;
    }

    public int getWeaponDamage(int typeRequesting) {
        if (typeRequesting == 1)
        {
            return targetDamage;
        }
        else
        {
            return spreadDamage;
        }
    }

    // Returns formatted weapon info for display
    public String getWeaponInfo() {
        return  "Target Weapon Damage: " + targetDamage + "\n" +
                "Spread Weapon Damage: " + spreadDamage;
    }
    // Getter for player ID
    public int getId() {
        return id;
    }

    public void sacrificeHealth(Player playerHealing)
    {
        takeDamage(hp);
        playerHealing.heal(hp);
    }

    // Convenience method for increasing damage (for a later version...)
    /**public void increaseDamage(int amount) {
    *    weaponDamage += amount;
    *}
    **/
    
}
