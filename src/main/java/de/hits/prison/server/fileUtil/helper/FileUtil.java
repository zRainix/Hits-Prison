package de.hits.prison.server.fileUtil.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.anno.Component;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Component
public abstract class FileUtil {

    @Autowired
    private static HitsPrison main;

    protected File file;
    protected YamlConfiguration cfg;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadConfig() {
        try {
            this.cfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    protected  void saveDefaultsConfig() {
        this.cfg.options().copyDefaults(true);
        saveConfig();
    }

    protected void saveConfig() {
        try {
            this.cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetConfig() {
        this.cfg = new YamlConfiguration();
        init();
        load();
    }

    public String getFileName() {
        return file.getName();
    }

    public abstract void init();

    public abstract void save();

    public abstract void load();

    public YamlConfiguration getConfig() {
        return this.cfg;
    }
}
