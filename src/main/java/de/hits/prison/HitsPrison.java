package de.hits.prison;

import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.dao.PrisonPlayerDao;
import de.hits.prison.model.entity.PlayerCurrency;
import de.hits.prison.model.entity.PrisonPlayer;
import de.hits.prison.model.helper.HibernateUtil;
import de.hits.prison.scheduler.SchedulerManager;
import de.hits.prison.scheduler.impl.SaveFileUtilScheduler;
import de.hits.prison.util.FileUtilManager;
import de.hits.prison.util.impl.SettingsUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class HitsPrison extends JavaPlugin {

    private static HitsPrison main;

    private Logger logger = Bukkit.getLogger();

    // Manager
    private FileUtilManager fileUtilManager = new FileUtilManager();
    private SchedulerManager schedulerManager = new SchedulerManager();

    // File utils
    private SettingsUtil settingsUtil;

    // Schedulers
    private SaveFileUtilScheduler saveFileUtilScheduler;

    // DAOs
    private PrisonPlayerDao prisonPlayerDao;
    private PlayerCurrencyDao playerCurrencyDao;

    @Override
    public void onEnable() {
        logger.info("Starting " + this.getName() + "...");

        main = this;

        registerUtils(this.fileUtilManager);
        registerSchedulers(this.schedulerManager);
        registerHibernate();

        logger.info("Plugin " + this.getName() + ": STARTED");
    }

    private void registerUtils(FileUtilManager fileUtilManager) {
        // SettingsUtil
        this.settingsUtil = new SettingsUtil();
        this.fileUtilManager.registerFileUtil(this.settingsUtil);

        // Initialize and load all file utils
        this.fileUtilManager.initAll();
        this.fileUtilManager.loadAll();
    }

    private void registerSchedulers(SchedulerManager schedulerManager) {
        // SaveFileUtilScheduler
        this.saveFileUtilScheduler = new SaveFileUtilScheduler(this.fileUtilManager);
        this.saveFileUtilScheduler.start();
        this.schedulerManager.registerScheduler(this.saveFileUtilScheduler);
    }

    private void registerHibernate() {
        HibernateUtil.init(this);

        this.prisonPlayerDao = new PrisonPlayerDao();
        this.playerCurrencyDao = new PlayerCurrencyDao();
    }

    @Override
    public void onDisable() {
        logger.info("Stopping " + this.getName() + "...");

        this.fileUtilManager.saveAll();

        HibernateUtil.shutdown();

        logger.info("Plugin " + this.getName() + ": STOPPED");
    }

    public static HitsPrison getMain() {
        return main;
    }

    public SettingsUtil getSettingsUtil() {
        return this.settingsUtil;
    }
}
