package Model;

public class Monster {
    private String id;
    private String name;
    private String description;
    private String locationFound;
    private int lvl;

    public Monster(String id, String name, String description, String locationFound) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lvl = 1; // Default level
        this.locationFound = locationFound;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocationFound() {
        return locationFound;
    }

    public int getLvl() {
        return lvl;
    }
}
