package de.hits.prison;

import de.hits.prison.command.helper.ArgumentParserRegistry;
import de.hits.prison.mechanic.helper.BaseManager;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.dao.PrisonPlayerDao;
import de.hits.prison.model.helper.ClassScanner;
import de.hits.prison.model.helper.HibernateUtil;
import de.hits.prison.scheduler.helper.SchedulerManager;
import de.hits.prison.scheduler.impl.SaveFileUtilScheduler;
import de.hits.prison.util.helper.FileUtilManager;
import de.hits.prison.util.impl.SettingsUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
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

        PluginManager pluginManager = Bukkit.getPluginManager();

        registerUtils(this.fileUtilManager);
        registerSchedulers(this.schedulerManager);
        registerHibernate();
        registerCommandParsers();
        registerCommands();
        registerEvents(pluginManager);
        registerManagers(pluginManager);

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

    private void registerCommandParsers() {
        try {
            ArgumentParserRegistry.registerAll("de.hits.prison.command.parser");
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.severe("Error while initializing command type parsers: " + e.getMessage());
        }
    }

    private void registerCommands() {

    }

    private void registerEvents(PluginManager pluginManager) {

    }


    private void registerManagers(PluginManager pluginManager) {
        Set<Class<?>> managers = ClassScanner.getClasses("de.hits.prison", BaseManager.class);

        try {
            for (Class<?> manager : managers) {
                BaseManager baseManager = (BaseManager) manager.getConstructor().newInstance();
                Method method = manager.getMethod("register", HitsPrison.class, PluginManager.class);
                method.setAccessible(true);
                method.invoke(baseManager, this, pluginManager);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.severe("Error while initializing managers: " + e.getMessage());
        }
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

    public FileUtilManager getFileUtilManager() {
        return this.fileUtilManager;
    }

    public SettingsUtil getSettingsUtil() {
        return this.settingsUtil;
    }

    public PrisonPlayerDao getPrisonPlayerDao() {
        return this.prisonPlayerDao;
    }

    public PlayerCurrencyDao getPlayerCurrencyDao() {
        return this.playerCurrencyDao;
    }
}
