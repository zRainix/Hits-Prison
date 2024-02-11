package de.hits.prison.mechanic.pickaxe.helper;

public class PickaxeEnchantment {

    private String name;
    private int maxLevel;

    public PickaxeEnchantment(String name, int maxLevel) {
        this.name = name;
        this.maxLevel = maxLevel;
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
