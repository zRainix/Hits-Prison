package de.hits.prison;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.helper.AutowiredManager;
import de.hits.prison.command.helper.ArgumentParserRegistry;
import de.hits.prison.mechanic.helper.BaseManager;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.dao.PrisonPlayerDao;
import de.hits.prison.model.helper.ClassScanner;
import de.hits.prison.model.helper.HibernateUtil;
import de.hits.prison.scheduler.helper.SchedulerManager;
import de.hits.prison.mechanic.server.scheduler.SaveFileUtilScheduler;
import de.hits.prison.fileUtil.helper.FileUtilManager;
import de.hits.prison.mechanic.server.fileUtil.SettingsUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Logger;

public final class HitsPrison extends JavaPlugin {

    private Logger logger = Bukkit.getLogger();

    // Manager
    private FileUtilManager fileUtilManager = new FileUtilManager();
    private SchedulerManager schedulerManager = new SchedulerManager();

    @Autowired
    private static SaveFileUtilScheduler saveFileUtilScheduler;

    @Override
    public void onEnable() {
        logger.info("Starting " + this.getName() + "...");

        AutowiredManager.register(this);

        PluginManager pluginManager = Bukkit.getPluginManager();

        registerUtils(this.fileUtilManager);
        registerSchedulers(this.schedulerManager);
        registerHibernate();
        registerCommandParsers();
        registerManagers(pluginManager);


        logger.info("Plugin " + this.getName() + ": STARTED");
    }

    private void registerUtils(FileUtilManager fileUtilManager) {
        AutowiredManager.register(fileUtilManager);

        fileUtilManager.registerAllFileUtils("de.hits.prison");
    }

    private void registerSchedulers(SchedulerManager schedulerManager) {
        AutowiredManager.register(schedulerManager);

        schedulerManager.registerAllSchedulers("de.hits.prison");
    }

    private void registerHibernate() {
        HibernateUtil.init(this);

        HibernateUtil.registerAllRepositories("de.hits.prison");
    }

    private void registerCommandParsers() {
        try {
            ArgumentParserRegistry.registerAll("de.hits.prison.command.parser");
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.severe("Error while initializing command type parsers: " + e.getMessage());
        }
    }

    private void registerManagers(PluginManager pluginManager) {
        Set<Class<? extends BaseManager>> managers = ClassScanner.getClasses("de.hits.prison", BaseManager.class);

        try {
            for (Class<?> manager : managers) {
                BaseManager baseManager = (BaseManager) manager.getConstructor().newInstance();
                Method method = manager.getMethod("register", HitsPrison.class, PluginManager.class);
                method.setAccessible(true);
                method.invoke(baseManager, this, pluginManager);
                AutowiredManager.register(baseManager);
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
}
