package de.hits.customhoe;

import de.hits.mysql.MySQL;
import de.hits.scheduler.SchedulerManager;
import de.hits.scheduler.impl.SaveFileUtilScheduler;
import de.hits.util.FileUtilManager;
import de.hits.util.impl.SettingsUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;

public final class CustomHoe extends JavaPlugin {

    private static CustomHoe main;

    private FileUtilManager fileUtilManager = new FileUtilManager();
    private SettingsUtil settingsUtil;

    private SchedulerManager schedulerManager = new SchedulerManager();

    private SaveFileUtilScheduler saveFileUtilScheduler;

    @Override
    public void onEnable() {
        main = this;

        System.out.println("PLUGIN - §aSTARTED");

        registerUtils(this.fileUtilManager);
        registerSchedulers(this.schedulerManager);

        this.settingsUtil.load();
        MySQL mySQL = new MySQL(this.settingsUtil);
        mySQL.connect();

        String selectQuery = "SELECT * FROM weloverainix;";
        ResultSet resultSet = mySQL.executeQuery(selectQuery);

        mySQL.disconnect();

    }

    private void registerUtils(FileUtilManager fileUtilManager) {
        this.settingsUtil = new SettingsUtil();
        this.fileUtilManager.registerFileUtil(this.settingsUtil);
        this.fileUtilManager.initAll();
    }

    private void registerSchedulers(SchedulerManager schedulerManager) {
        Bukkit.getScheduler().runTaskTimer(CustomHoe.getMain(), () ->  {
            this.saveFileUtilScheduler = new SaveFileUtilScheduler(this.fileUtilManager);
            this.schedulerManager.registerScheduler(this.saveFileUtilScheduler);
            this.saveFileUtilScheduler.start();
        }, 0, 5*60*20L);
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
