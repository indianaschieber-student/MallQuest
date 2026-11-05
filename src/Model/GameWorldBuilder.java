package Model;

import java.io.*;
import java.util.*;

public class GameWorldBuilder {

    /*public List <Item> createGameItems() {

    }*/
    /*public List<Puzzle> createGamePuzzles() {

    }*/

    /*
    This method creates all the attack for the monsters in the game,
    and returns a list of MonsterAttack objects.
    This needs to be done before creating the monsters,
    so that the attacks can be assigned to the monsters.

     */
    public List<MonsterAttack> createMonsterAttacks() throws FileNotFoundException {
        List<MonsterAttack> attacks = new ArrayList<>();

        File file = new File("MonsterAttacks.txt");
        Scanner fileReader = new Scanner(file);

        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();

            //Note: Skip empty lines and comments (lines starting with #)
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }


            String[] data = line.split("\\|");

            if(data.length != 5){
                System.out.println("!!!!! Invalid attack data format: " + line + " !!!!!");
                continue;
            }

            String id = data[0].trim();
            String atkName = data[1].trim();
            int damage = Integer.parseInt(data[2].trim());
            String desc = data[3].trim();
            String effect = data[4].trim();

            MonsterAttack newAttack = new MonsterAttack(id, atkName, damage, desc, effect);

            attacks.add(newAttack);
            fileReader.close();
        }

        return attacks;

    }
    public List<Monster> createGameMonsters(List<MonsterAttack> allAttacks) throws FileNotFoundException {
        List<Monster> monsters = new ArrayList<>();

        File file = new File("Monsters.txt");
        Scanner fileReader = new Scanner(file);

        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();

            //Note: Skip empty lines and comments (lines starting with #)
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }

            String[] data = line.split("\\|");

            if(data.length != 6){
                System.out.println("!!!!! Invalid monster data format: " + line + " !!!!!");
                continue;
            }

            String id = data[0].trim();
            String name = data[1].trim();
            String description = data[2].trim();
            String locationFound = data[3].trim();

            int lvl = Integer.parseInt(data[4].trim());
            int maxHp = Integer.parseInt(data[5].trim());

            int attack = 0;

            // Get the attack IDs assigned to this monster
            String[] attackIds = data[7].split(",");

            ArrayList<MonsterAttack> monsterAttacks = new ArrayList<>();

            for (String attackId : attackIds) {

                attackId = attackId.trim();

                // Find the corresponding MonsterAttack object
                for (MonsterAttack monsterAttack : allAttacks) {

                    if (monsterAttack.getId().equals(attackId)) {

                        monsterAttacks.add(monsterAttack);
                        break;
                    }
                }
            }

            Monster newMonster = new Monster(id, name, description, locationFound, lvl, maxHp, attack, monsterAttacks);

            monsters.add(newMonster);
        }
        fileReader.close();

        return monsters;
    }
    //The method to create the Rooms should be last; the Items, Puzzles, and Monsters should
    // be created first, so that the Rooms can then be populated with Items, Puzzles, and Monsters
    /*public Map <Integer, Room> createGameMap() {
        This method reads the game map from Rooms.txt and creates the game map as a Hash Map
        of Room objects with the roomId as the key and the obect as the value.
    }*/
}
