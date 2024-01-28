package de.hits.util.impl;

import de.hits.util.FileUtil;

public class SettingsUtil extends FileUtil {

    private String prefix;

    public SettingsUtil() {
        super("settings.yaml");

        this.prefix = "§c[§a§lHOE§c] §7";
    }

    @Override
    public void init() {
        getConfig().addDefault("prefix", this.prefix);
        saveConfig();
    }

    @Override
    public void save() {
        getConfig().set("prefix", this.prefix);
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();

        this.prefix = getConfig().getString("prefix");
    }
}
