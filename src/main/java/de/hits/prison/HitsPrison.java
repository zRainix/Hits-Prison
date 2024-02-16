package de.hits.prison;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.command.helper.ArgumentParserRegistry;
import de.hits.prison.base.fileUtil.helper.FileUtilManager;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.base.model.helper.ClassScanner;
import de.hits.prison.base.model.helper.HibernateUtil;
import de.hits.prison.base.scheduler.helper.SchedulerManager;
import de.hits.prison.base.screen.ScreenManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public final class HitsPrison extends JavaPlugin {

    private final Logger logger = Bukkit.getLogger();

    // Manager
    private final FileUtilManager fileUtilManager = new FileUtilManager();
    private final SchedulerManager schedulerManager = new SchedulerManager();

    @Autowired
    private static ScreenManager screenManager;

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
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            logger.severe("Error while initializing command type parsers: " + e.getMessage());
        }
    }

    private void registerManagers(PluginManager pluginManager) {
        Set<Class<? extends Manager>> managers = ClassScanner.getClassesBySuperclass(getClass().getPackageName(), Manager.class);

        try {
            for (Class<?> manager : managers) {
                Manager baseManager = (Manager) manager.getConstructor().newInstance();
                AutowiredManager.register(baseManager);
                baseManager.register(this, pluginManager);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            logger.log(Level.SEVERE, "Error while initializing managers.", e);
        }
    }

    public void registerCommand(String commandName, CommandExecutor commandExecutor) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            logger.warning("Error while registering command " + commandName + ": Command not defined.");
            return;
        }
        command.setExecutor(commandExecutor);

        if (!(commandExecutor instanceof TabCompleter)) {
            return;
        }

        command.setTabCompleter((TabCompleter) commandExecutor);
    }

    @Override
    public void onDisable() {
        logger.info("Stopping " + this.getName() + "...");

        screenManager.closeAllScreens();

        this.fileUtilManager.saveAll();

        HibernateUtil.shutdown();


        logger.info("Plugin " + this.getName() + ": STOPPED");
    }
}
