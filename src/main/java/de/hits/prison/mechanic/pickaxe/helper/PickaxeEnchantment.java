package de.hits.prison.mechanic.pickaxe.helper;

public class PickaxeEnchantment {

    private String name;
    private int maxLevel;

    private int type;

    public PickaxeEnchantment(String name, int maxLevel, int type) {
        this.name = name;
        this.maxLevel = maxLevel;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }
}
