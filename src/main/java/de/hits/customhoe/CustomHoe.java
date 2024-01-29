package de.hits.customhoe;

import de.hits.scheduler.CustomScheduler;
import de.hits.scheduler.SchedulerManager;
import de.hits.scheduler.impl.SaveThoseSchedulers;
import de.hits.util.FileUtil;
import de.hits.util.FileUtilManager;
import de.hits.util.impl.SettingsUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomHoe extends JavaPlugin {

    private static CustomHoe main;

    private FileUtilManager fileUtilManager = new FileUtilManager();
    private SettingsUtil settingsUtil;

    private SchedulerManager schedulerManager = new SchedulerManager();

    private SaveThoseSchedulers saveThoseSchedulers;

    @Override
    public void onEnable() {
        main = this;

        System.out.println("PLUGIN - §aSTARTED");

        registerUtils(this.fileUtilManager);
        registerSchedulers(this.schedulerManager);

    }

    private void registerUtils(FileUtilManager fileUtilManager) {
        this.settingsUtil = new SettingsUtil();
        this.fileUtilManager.registerFileUtil(this.settingsUtil);
        this.fileUtilManager.initAll();
    }

    private void registerSchedulers(SchedulerManager schedulerManager) {
        this.saveThoseSchedulers = new SaveThoseSchedulers(this.fileUtilManager);
        this.schedulerManager.registerScheduler(this.saveThoseSchedulers);
        this.saveThoseSchedulers.start();
    }

    @Override
    public void onDisable() {
        System.out.println("PLUGIN - §cSTOPPED");

        this.fileUtilManager.saveAll();
    }

    public static CustomHoe getMain() {
        return main;
    }

    public SettingsUtil getSettingsUtil() {
        return this.settingsUtil;
    }
}
