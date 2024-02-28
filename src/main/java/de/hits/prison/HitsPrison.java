package de.hits.prison;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.command.helper.ArgumentParserRegistry;
import de.hits.prison.base.command.helper.SimpleCommand;
import de.hits.prison.base.fileUtil.helper.FileUtilManager;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.base.model.helper.ClassScanner;
import de.hits.prison.base.model.helper.HibernateUtil;
import de.hits.prison.base.scheduler.helper.SchedulerManager;
import de.hits.prison.base.screen.ScreenManager;
import de.hits.prison.mine.helper.MineHelper;
import de.hits.prison.mine.helper.MineWorld;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
    @Autowired
    private static MineHelper mineHelper;

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

        loadMines();

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
        Set<Manager> baseManagers = new HashSet<>();
        try {
            for (Class<?> manager : managers) {
                Manager baseManager = (Manager) manager.getConstructor().newInstance();
                baseManagers.add(baseManager);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            logger.log(Level.SEVERE, "Error while initializing managers.", e);
        }
        baseManagers.stream().sorted(Comparator.comparingInt(manager -> ((Manager) manager).getPriority().getSlot()).reversed()).forEach(manager -> {
            AutowiredManager.register(manager);
            manager.register(this, pluginManager);
        });
    }

    private void loadMines() {
        mineHelper.getMineWorldMap().values().forEach(MineWorld::updateMine);
    }


    public void registerCommand(SimpleCommand simpleCommand) {
        registerCommand(simpleCommand.getCommandName(), simpleCommand.getAliases(), simpleCommand);
    }

    public void registerCommand(String commandName, List<String> aliases, CommandExecutor commandExecutor) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            logger.warning("Error while registering command " + commandName + ": Command not defined.");
            return;
        }
        command.setAliases(aliases);
        command.setExecutor(commandExecutor);

        if (!(commandExecutor instanceof TabCompleter)) {
            return;
        }

        command.setTabCompleter((TabCompleter) commandExecutor);
    }

    @Override
    public void onDisable() {
        logger.info("Stopping " + this.getName() + "...");

        HibernateUtil.shutdown();

        screenManager.closeAllScreens();

        this.fileUtilManager.saveAll();

        World mainWorld = Bukkit.getWorld("world");

        for (World world : Bukkit.getWorlds()) {
            if (world.getName().startsWith("template-mine-") || world.getName().startsWith("player-mine-")) {
                world.getPlayers().forEach(player -> player.teleport(mainWorld.getSpawnLocation()));
                Bukkit.unloadWorld(world, false);
                try {
                    FileUtils.deleteDirectory(world.getWorldFolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        mineHelper.getMineWorldMap().clear();

        logger.info("Plugin " + this.getName() + ": STOPPED");
    }

    public File getBaseFolder() {
        return getDataFolder().getParentFile().getParentFile();
    }
}
