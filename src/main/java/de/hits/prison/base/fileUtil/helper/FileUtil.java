package de.hits.prison.base.fileUtil.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public abstract class FileUtil {

    private final Logger logger = Bukkit.getLogger();

    @Autowired
    private static HitsPrison main;

    protected File file;
    protected YamlConfiguration cfg;

    public FileUtil(File file) {
        this.file = file;
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public FileUtil(String fileName) {
        if ((!fileName.toLowerCase().endsWith(".yml") && !fileName.toLowerCase().endsWith(".yaml"))) {
            fileName += ".yml";
        }
        this.file = new File(main.getDataFolder(), fileName);
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void createFileIfNotExists() {
        try {
            File parentDirectory = file.getParentFile();
            if (parentDirectory != null && !parentDirectory.exists()) {
                parentDirectory.mkdirs();
            }

            if (!this.file.exists()) {
                this.file.createNewFile();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while creating file " + getFileName(), e);
        }
    }

    protected void loadConfig() {
        try {
            createFileIfNotExists();
            this.cfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            logger.log(Level.SEVERE, "Error while loading config for " + getFileName(), e);
        }
    }

    protected void saveDefaultsConfig() {
        this.cfg.options().copyDefaults(true);
        saveConfig();
    }

    protected void saveConfig() {
        try {
            this.cfg.save(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while saving config for " + getFileName(), e);
        }
    }

    public void resetConfig() {
        this.file.delete();
        init();
        load();
        save();
    }

    public String getFileName() {
        return file.getName();
    }

    public abstract void init();

    public abstract void save();

    public abstract void load();
}
