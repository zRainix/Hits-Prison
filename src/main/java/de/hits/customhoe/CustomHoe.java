package de.hits.customhoe;

import de.hits.scheduler.CustomScheduler;
import de.hits.scheduler.SchedulerManager;
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
    @Override
    public void onEnable() {
        main = this;

        System.out.println("PLUGIN - §aSTARTED");

        registerUtils(this.fileUtilManager);

        this.fileUtilManager.initAll();


    }

    private void registerUtils(FileUtilManager fileUtilManager) {

        this.settingsUtil = new SettingsUtil();
        fileUtilManager.registerFileUtil(this.settingsUtil);
    }

    @Override
    public void onDisable() {
        System.out.println("PLUGIN - §cSTOPPED");

        this.fileUtilManager.saveAll();
    }

    private void registerSchedulers(SchedulerManager schedulerManager, int delay, int period) {
        this.customScheduler = new CustomScheduler(delay, period) {
            @Override
            public void run() {
                System.out.println("CustomScheduler is running for " + delay + " and " + period);
            }
        };

        schedulerManager.registerScheduler(this.customScheduler);
        this.customScheduler.start();
    }

    public static CustomHoe getMain() {
        return main;
    }

    public SettingsUtil getSettingsUtil() {
        return settingsUtil;
    }
}
