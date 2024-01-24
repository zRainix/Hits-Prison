package de.hits.util;

import de.hits.customhoe.CustomHoe;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Properties;

public abstract class FileUtil {

    private CustomHoe main = CustomHoe.getMain();

    private File file;
    private YamlConfiguration cfg;

    public FileUtil(String fileName) {
        if((!fileName.toLowerCase().endsWith(".yml") && !fileName.toLowerCase().endsWith(".yaml"))) {
            fileName += ".yml";
        }
        this.file = new File(main.getDataFolder(), fileName);
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void createFileIfNotExists() {
        if(!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void loadConfig() {
        try {
            this.cfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    protected void saveConfig() {
        try {
            this.cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void init();

    public abstract void save();

    public abstract void load();
}
