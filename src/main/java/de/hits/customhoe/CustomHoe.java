package de.hits.customhoe;

import org.bukkit.plugin.java.JavaPlugin;

public final class CustomHoe extends JavaPlugin {

    public static String PREFIX = "§c[§a§lHOE§c] §7";

    @Override
    public void onEnable() {
        System.out.println(PREFIX + "started");

    }

    @Override
    public void onDisable() {
        System.out.println(PREFIX + "stopped");
    }
}
