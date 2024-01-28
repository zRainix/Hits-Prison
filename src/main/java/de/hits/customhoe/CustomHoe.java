package de.hits.customhoe;

import de.hits.util.FileUtil;
import de.hits.util.FileUtilManager;
import de.hits.util.impl.SettingsUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomHoe extends JavaPlugin {

    private static CustomHoe main;

    private FileUtilManager fileUtilManager = new FileUtilManager();

    private SettingsUtil settingsUtil;

    @Override
    public void onEnable() {
        main = this;

        System.out.println("PLUGIN - §aSTARTED");

        registerUtils(fileUtilManager);

        fileUtilManager.initAll();
    }

    private void registerUtils(FileUtilManager fileUtilManager) {
        this.settingsUtil = new SettingsUtil();
        fileUtilManager.registerFileUtil(this.settingsUtil);
    }

    @Override
    public void onDisable() {
        System.out.println("PLUGIN - §cSTOPPED");

        fileUtilManager.saveAll();
    }

    public static CustomHoe getMain() {
        return main;
    }

    public SettingsUtil getSettingsUtil() {
        return settingsUtil;
    }
}
