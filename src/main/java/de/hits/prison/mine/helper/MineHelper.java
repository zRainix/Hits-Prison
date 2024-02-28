package de.hits.prison.mine.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerMineDao;
import de.hits.prison.base.model.entity.PlayerMine;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.mine.fileUtil.MineTemplateUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MineHelper {

    private final Logger logger = Bukkit.getLogger();

    @Autowired
    private static HitsPrison main;
    @Autowired
    private static PlayerMineDao playerMineDao;

    List<RegisteredMineTemplate> registeredTemplates;
    Map<String, MineWorld> mineWorldMap;

    public MineHelper() {
        this.registeredTemplates = new ArrayList<>();
        this.mineWorldMap = new HashMap<>();

        registerTemplates();
    }

    private void registerTemplates() {
        logger.info("Registering templates...");
        File templatesFolder = new File(main.getBaseFolder(), "mineTemplates");
        templatesFolder.mkdirs();
        templatesFolder.mkdir();
        File[] templates = templatesFolder.listFiles();
        if (templates.length == 0) {
            logger.info("No templated found.");
            return;
        }
        for (File template : templates) {
            if (!template.isDirectory())
                continue;
            logger.info("Template: " + template.getName());
            File mineFile = new File(template, "mine.yml");
            if (!mineFile.exists()) {
                logger.warning("- Mine File (mine.yml) does not exist.");
                continue;
            }
            registerTemplate(template, mineFile);
        }
    }

    private void registerTemplate(File templateFolder, File mineFile) {
        logger.info("Registering template from folder: " + templateFolder.getName());
        try {
            MineTemplateUtil mineTemplateUtil = new MineTemplateUtil(new File(templateFolder, mineFile.getName()));
            mineTemplateUtil.init();
            mineTemplateUtil.load();

            String worldName = "mine-template-" + mineTemplateUtil.getName();

            World templateWorld = loadWorld(worldName, templateFolder);

            File worldFolder = new File(main.getBaseFolder(), worldName);
            registeredTemplates.add(new RegisteredMineTemplate(templateWorld, templateFolder, mineTemplateUtil));
            logger.info("Mine template registered: " + mineTemplateUtil.getName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while registering template: " + templateFolder.getName(), e);
        }
    }

    public static World loadWorld(String name, File from) throws IOException {
        return loadWorld(name, from, true);
    }

    public static World loadWorld(String name, File from, boolean deleteFolder) throws IOException {
        World world = Bukkit.getWorld(name);
        if (world != null) {
            if (!deleteFolder)
                return world;

            World mainWorld = Bukkit.getWorld("world");

            world.getPlayers().forEach(player -> {
                player.teleport(mainWorld.getSpawnLocation());
            });
            Bukkit.unloadWorld(world, false);
        }

        File worldFolder = new File(main.getBaseFolder(), name);
        if (deleteFolder) {
            FileUtils.deleteDirectory(worldFolder);
            FileUtils.copyDirectory(from, worldFolder);
        } else if (!new File(worldFolder, "mine.yml").exists()) {
            FileUtils.copyDirectory(from, worldFolder);
        }

        WorldCreator worldCreator = new WorldCreator(worldFolder.getName());
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.hardcore(false);
        world = Bukkit.createWorld(worldCreator);
        return world;
    }

    public RegisteredMineTemplate getRegisteredMineTemplate(String name) {
        for (RegisteredMineTemplate template : registeredTemplates) {
            if (template.getTemplateUtil().getName().equals(name))
                return template;
        }
        return null;
    }

    public List<RegisteredMineTemplate> getRegisteredTemplates() {
        return registeredTemplates;
    }

    public Map<String, MineWorld> getMineWorldMap() {
        return mineWorldMap;
    }

    public boolean isMineWorld(World world) {
        return getMineWorld(world) != null;
    }

    public MineWorld getMineWorld(World world) {
        for (MineWorld mineWorld : mineWorldMap.values()) {
            if (mineWorld.getWorld() == world)
                return mineWorld;
        }
        return null;
    }

    public MineWorld getMineWorld(PrisonPlayer prisonPlayer) {
        String uuid = prisonPlayer.getPlayerUuid();
        return mineWorldMap.getOrDefault(uuid, null);
    }

    public void teleportPlayerToMine(Player player, MineWorld mineWorld) {
        teleportPlayerToMine(player, mineWorld.getWorld(), mineWorld.getMineTemplateUtil());
    }

    public void teleportPlayerToMine(Player player, World world, MineTemplateUtil mineTemplateUtil) {
        player.teleport(world.getBlockAt(mineTemplateUtil.getCenterBlock()).getRelative(0, 1, 0).getLocation());
    }

    public MineWorld generateMineWorld(PrisonPlayer prisonPlayer, RegisteredMineTemplate template) throws IOException {
        PlayerMine playerMine = playerMineDao.findByPrisonPlayer(prisonPlayer);
        if (playerMine == null)
            return null;

        String uuid = prisonPlayer.getPlayerUuid();
        if (mineWorldMap.containsKey(uuid)) {
            return mineWorldMap.get(uuid);
        }

        MineWorld mineWorld = new MineWorld(template.getTemplateFolder(), template.getTemplateUtil(), prisonPlayer);
        mineWorldMap.put(uuid, mineWorld);

        return getMineWorld(prisonPlayer);
    }

    public boolean isTemplateWorld(World world) {
        for (RegisteredMineTemplate template : registeredTemplates) {
            if (template.getTemplateWorld() == world)
                return true;
        }
        return false;
    }

    public static class RegisteredMineTemplate {

        World templateWorld;
        File templateFolder;
        MineTemplateUtil templateUtil;

        public RegisteredMineTemplate(World templateWorld, File templateFolder, MineTemplateUtil templateUtil) {
            this.templateWorld = templateWorld;
            this.templateFolder = templateFolder;
            this.templateUtil = templateUtil;
        }

        public World getTemplateWorld() {
            return templateWorld;
        }

        public void setTemplateWorld(World templateWorld) {
            this.templateWorld = templateWorld;
        }

        public File getTemplateFolder() {
            return templateFolder;
        }

        public void setTemplateFolder(File templateFolder) {
            this.templateFolder = templateFolder;
        }

        public MineTemplateUtil getTemplateUtil() {
            return templateUtil;
        }

        public void setTemplateUtil(MineTemplateUtil templateUtil) {
            this.templateUtil = templateUtil;
        }
    }
}
