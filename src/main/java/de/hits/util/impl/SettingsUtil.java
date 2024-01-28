package de.hits.util.impl;

import de.hits.util.FileUtil;

public class SettingsUtil extends FileUtil {

    private String prefix;

    public SettingsUtil() {
        super("settings.yaml");

        prefix = "defaultPrefix";
    }

    @Override
    public void init() {
        getConfig().addDefault("key1", "batch1");
        getConfig().addDefault("key2", "batch2");
        getConfig().addDefault("key3", "batch3");
        getConfig().addDefault("key4", "batch4");
        getConfig().addDefault("prefix", "§c[§a§lHOE§c] §7");
        saveConfig();
    }

    @Override
    public void save() {
        getConfig().set("key1", "IchLiebeKeks");
        getConfig().set("key2", "IchLiebeWaffel");
        getConfig().set("key3", "IchLiebeEiskönigin");
        getConfig().set("prefix", prefix);
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();

        String value1 = getConfig().getString("key1");
        String value2 = getConfig().getString("key2");
        String value3 = getConfig().getString("key3");
        prefix = getConfig().getString("prefix");

        System.out.println("Loaded settings - key1: " + value1 + ", key2: " + value2 + ", key3: " + value3 + ", prefix: " + prefix);
    }
}
