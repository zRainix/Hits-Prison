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

        registerUtils(fileUtilManager);

        fileUtilManager.initAll();

        CustomScheduler scheduler = new CustomScheduler(20, 20) {
            int seconds = 0;

            public void run() {
                Bukkit.broadcastMessage("§aAktuell bei Sekunde §6" + seconds + "§a.");
                seconds++;
            }
        };

        this.schedulerManager.registerScheduler(scheduler);
        scheduler.start();
    }

    private void registerUtils(FileUtilManager fileUtilManager) {
        // Settings Util
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
