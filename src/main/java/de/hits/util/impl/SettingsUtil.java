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
        this.cfg.addDefault("prefix", this.prefix);
        saveConfig();
    }

    @Override
    public void save() {
        this.cfg.set("prefix", prefix);
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();

        prefix = this.cfg.getString("prefix");
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String newprefix) {
        this.prefix = newprefix;
    }
}
