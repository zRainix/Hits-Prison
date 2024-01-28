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
    private CustomScheduler customScheduler;
    private SaveThoseSchedulers saveThoseSchedulers;

    @Override
    public void onEnable() {
        main = this;

        System.out.println("PLUGIN - §aSTARTED");

        registerUtils(this.fileUtilManager);
        registerSchedulers(this.schedulerManager, 20L, 100L);

    }

    private void registerUtils(FileUtilManager fileUtilManager) {
        this.settingsUtil = new SettingsUtil();
        this.fileUtilManager.registerFileUtil(this.settingsUtil);
        this.fileUtilManager.initAll();
    }

    private void registerSchedulers(SchedulerManager schedulerManager, long delay, long period) {
        this.customScheduler = new CustomScheduler(delay, period) {
            @Override
            public void run() {
                System.out.println("SaveAllUtilFiles will run for " + delay + " " + period);
            }
        };

        this.saveThoseSchedulers = new SaveThoseSchedulers(this.fileUtilManager);
        System.out.println("save");

        this.schedulerManager.registerScheduler(this.customScheduler);
        schedulerManager.registerScheduler(this.saveThoseSchedulers);

        this.customScheduler.start();
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
