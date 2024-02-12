package de.hits.prison;

import de.hits.prison.base.scheduler.SaveFileUtilScheduler;
import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.helper.AutowiredManager;
import de.hits.prison.server.command.helper.ArgumentParserRegistry;
import de.hits.prison.server.fileUtil.helper.FileUtilManager;
import de.hits.prison.server.helper.Manager;
import de.hits.prison.server.model.helper.ClassScanner;
import de.hits.prison.server.model.helper.HibernateUtil;
import de.hits.prison.server.scheduler.helper.SchedulerManager;
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

        fileUtilManager.registerAllFileUtils(getClass().getPackageName());
    }

    private void registerSchedulers(SchedulerManager schedulerManager) {
        AutowiredManager.register(schedulerManager);

        schedulerManager.registerAllSchedulers(getClass().getPackageName());
    }

    private void registerHibernate() {
        HibernateUtil.init(this);

        HibernateUtil.registerAllRepositories(getClass().getPackageName());
    }

    private void registerCommandParsers() {
        try {
            ArgumentParserRegistry.registerAll();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.severe("Error while initializing command type parsers: " + e.getMessage());
        }
    }

    private void registerManagers(PluginManager pluginManager) {
        Set<Class<? extends Manager>> managers = ClassScanner.getClasses(getClass().getPackageName(), Manager.class);

        try {
            for (Class<?> manager : managers) {
                Manager baseManager = (Manager) manager.getConstructor().newInstance();
                AutowiredManager.register(baseManager);
                baseManager.register(this, pluginManager);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            logger.severe("Error while initializing managers: " + e.getMessage());
            e.printStackTrace();
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
