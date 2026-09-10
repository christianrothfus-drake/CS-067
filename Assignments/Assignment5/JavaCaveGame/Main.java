package Assignment5.JavaCaveGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Entry point for the Cave Game.
// Handles game setup, rounds, combat, rewards, and status display.

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ----- Game Setup -----
        System.out.print("How many players enter the cave? ");
        int playerCount = Integer.parseInt(scanner.nextLine());

        // Create players with starting HP
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player(20));
        }

        int round = 1;
        boolean gameRunning = true;
        int totalGoblinsKilled = 0;

        // Flavor text depending on party size
        if (playerCount > 1) {
            System.out.println("\nThe party enters the cave...");
        } else {
            System.out.println("\nYou enter the cave...");
        }

        // ===== MAIN GAME LOOP =====
        while (gameRunning) {

            System.out.println("\n===== ROUND " + round + " =====");

            // Small regeneration after the first round
            if (round > 1) {
                players.forEach(p -> p.heal(1));
                System.out.println("The party regains a bit of strength (+1 HP each).");
            }

            // ----- Spawn Enemies -----
            Enemy.resetIds();

            int minGoblins = Math.max(1, round / 2);
            int maxGoblins = round + 1;
            int goblinCount =
                (int)(Math.random() * (maxGoblins - minGoblins + 1)) + minGoblins;

            // Goblin attack power scales with rounds
            int maxAttackPower = 3 + round;

            List<Enemy> goblins = new ArrayList<>();
            for (int i = 0; i < goblinCount; i++) {
                goblins.add(new Enemy(10, maxAttackPower));
            }

            System.out.println(
                goblinCount == 1
                    ? "\nA goblin emerges from the darkness!"
                    : "\n" + goblinCount + " goblins emerge from the darkness!"
            );

            printStatus(players, goblins);

            // ===== COMBAT LOOP =====
            while (!players.isEmpty() && !goblins.isEmpty()) {

                // ----- Player Turns -----
                for (int p = 0; p < players.size() && !goblins.isEmpty(); p++) {
                    Player player = players.get(p);
                    player.startTurn();

                    System.out.println("\nPlayer " + player.getId() + ", choose an action:");
                    System.out.println("1) Attack (single target)");
                    System.out.println("2) Attack (target all)");
                    System.out.println("3) Defend");
                    System.out.println("4) Use Healing Potion");
                    System.out.println("5) Hone your weapons (+1 damage to weapons)");
                    System.out.println("6) Drains all remaining life to give it to another player");
                    System.out.print("> ");

                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            int damage = player.attack(1);
                            int targetIndex = (int)(Math.random() * goblins.size());
                            Enemy target = goblins.get(targetIndex);

                            target.takeDamage(damage);
                            System.out.println(
                                "Player " + player.getId() +
                                " hits Goblin " + target.getId() +
                                " for " + damage + " damage!"
                            );

                            if (!target.isAlive()) {
                                System.out.println("Goblin slain!");
                                goblins.remove(targetIndex);
                                totalGoblinsKilled++;
                            }
                            break;
                        
                        // NEW CASE -- I added this new option for the player to instead choose to do an area attack instead of a targeted attack.
                        case "2":
                            // Getting initial information
                            int spreadDamage = player.attack(2);
                            int totalBeginningGoblins = goblins.size();

                            // Iteratively dealing the damage to each goblin and removing them from the list if they die
                            // Note: We traverse the list backwards since going forwards causes index issues if a goblin gets killed and the list size changes
                            for (int goblinIdx=goblins.size()-1; goblinIdx>=0; goblinIdx--)
                            {
                                Enemy currentTarget = goblins.get(goblinIdx);
                                currentTarget.takeDamage(spreadDamage);

                                if (currentTarget.isAlive() == false)
                                {
                                    goblins.remove(goblinIdx);
                                    totalGoblinsKilled++;
                                }
                            }

                            int totalEndingGoblins = goblins.size();

                            System.out.println(
                                "Player " + player.getId() +
                                " hits all Goblins for " + spreadDamage + " damage!"
                            );

                            System.out.println((totalBeginningGoblins - totalEndingGoblins) + " goblins were slain!");
                            break;

                        case "3":
                            player.defend();
                            System.out.println("Player " + player.getId() + " prepares to block.");
                            break;

                        case "4":
                            player.usePotion();
                            break;

                        // NEW CASE -- I added this case to allow the player to hone their weapons instead of attacking
                        case "5":
                            player.upgradeWeapon(1);
                            player.vulnerable = true;

                            System.out.println("You have honed your weapons, but at what cost...");
                            break;

                        // NEW CASE -- I added this case to allow the player to sacrifice themselves to help an ally
                        case "6":
                            System.out.println("Enter the player number for the player you wish to heal");
                            System.out.print("> ");
                            String userInput = scanner.nextLine();
                            int targetId = Integer.valueOf(userInput);

                            // Handling if the player selects themselves.
                            if (targetId == player.getId())
                            {
                                System.out.println("You cannot sacrifice yourself to save yourself. Turn skipped.");
                                break;
                            }

                            // SACRIFICE! SACRIFICE! SACRIFICE! (insert crowd cheering loudly)
                            // Performing the sacrifice
                            int targetIdx = targetId - 1;
                            Player targetPlayer = players.get(targetIdx);
                            player.sacrificeHealth(targetPlayer);
                            
                            // Removing the sacrificed player from the player list      o7  o7  o7  o7  o7    (it looks like a little salute lol)
                            int sacrificedPlayerIdx = player.getId() - 1;
                            players.remove(sacrificedPlayerIdx);

                            System.out.println("You have chosen to give your remaining hit points to your ally. Your sacrifice will be remembered.");
                            break;
                            

                        default:
                            System.out.println("Invalid choice. Turn skipped.");
                    }
                }

                // ----- Goblin Turns -----
                for (int g = 0; g < goblins.size() && !players.isEmpty(); g++) {
                    Enemy goblin = goblins.get(g);

                    int targetIndex = (int)(Math.random() * players.size());
                    Player target = players.get(targetIndex);

                    int dmg = goblin.attack();
                    target.takeDamage(dmg);

                    System.out.println(
                        "Goblin " + goblin.getId() +
                        " hits Player " + target.getId() +
                        " for " + dmg + " damage!"
                    );

                    // Immediately remove defeated players
                    if (!target.isAlive()) {
                        System.out.println("Player " + target.getId() + " has been eliminated!");
                        players.remove(targetIndex);
                    }
                }

                if (!players.isEmpty() && !goblins.isEmpty()) {
                    printStatus(players, goblins);
                }

                if (goblins.isEmpty()) {
                    System.out.println("\nAll goblins have been slain!");
                }
            }

            // ===== ROUND END =====
            if (players.isEmpty()) {
                System.out.println("You fall in round " + round + "...");
                break;
            }

            System.out.println("You survive round " + round + "!");
            System.out.println("\n--- Survivors ---");
            players.forEach(p ->
                System.out.println("Player " + p.getId() + " HP: " + p.getHp())
            );

            // ----- Rewards -----
            System.out.println("\nChoose a reward:");
            System.out.println("1) Heal all players (+2 HP)");
            System.out.println("2) Gain 1 healing potion per player");
            System.out.println("3) Upgrade weapons (+1 damage)");
            System.out.print("> ");

            String rewardChoice = scanner.nextLine();

            switch (rewardChoice) {
                case "1":
                    players.forEach(p -> p.heal(2));
                    System.out.println("The party feels rejuvenated!");
                    break;

                case "2":
                    players.forEach(Player::addPotion);
                    System.out.println("Each player receives a healing potion!");
                    break;

                case "3":
                    players.forEach(p -> {
                        p.upgradeWeapon(1);
                        System.out.println(
                            "Player " + p.getId() + " upgraded weapon! " + p.getWeaponInfo()
                        );
                    });
                    break;

                default:
                    System.out.println("No reward gained.");
            }

            round++;
        }

        // ----- Game Summary -----
        System.out.println("\nGame Over");
        System.out.println(
            "You reached round " + round +
            " and slew " + totalGoblinsKilled + " goblins."
        );

        scanner.close();
    }

    // Displays current HP, potions, and enemy status
    private static void printStatus(List<Player> players, List<Enemy> goblins) {

        System.out.println("\n--- Party Status ---");
        for (Player p : players) {
            System.out.println(
                "Player " + p.getId() +
                " HP: " + p.getHp() +
                " | Potions: " + p.getPotions()
            );
        }

        if (goblins.isEmpty()) {
            System.out.println("\n--- All Enemies Defeated ---");
            return;
        }

        System.out.println("\n--- Goblin Status ---");
        for (Enemy g : goblins) {
            System.out.println(
                "Goblin " + g.getId() +
                " HP: " + g.getHp() +
                " | Attack Power: " + g.getAttackDamage()
            );
        }
    }
}
