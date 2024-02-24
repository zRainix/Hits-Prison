package de.hits.prison.mine.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.mine.fileUtil.MineTemplateUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MineHelper {

    private final Logger logger = Bukkit.getLogger();

    @Autowired
    private static HitsPrison main;

    private final File baseFolder;

    Map<World, MineTemplateUtil> registeredTemplates;

    public MineHelper() {
        this.baseFolder = main.getDataFolder().getParentFile().getParentFile();

        this.registeredTemplates = new HashMap<>();

        registerTemplates();
    }

    private void registerTemplates() {
        logger.info("Registering templates...");
        File templatesFolder = new File(baseFolder, "mineTemplates");
        templatesFolder.mkdirs();
        templatesFolder.mkdir();
        File[] templates = templatesFolder.listFiles();
        if(templates.length == 0) {
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
            World templateWorld = Bukkit.getWorld(templateFolder.getName());
            if (templateWorld != null) {
                Bukkit.unloadWorld(templateWorld, false);
            }
            File worldFolder = new File(baseFolder, templateFolder.getName());
            delete(worldFolder);

            FileUtils.copyDirectory(templateFolder, worldFolder);

            WorldCreator worldCreator = new WorldCreator(worldFolder.getName());
            worldCreator.environment(World.Environment.NORMAL);
            worldCreator.hardcore(false);
            templateWorld = Bukkit.createWorld(worldCreator);

            MineTemplateUtil mineTemplateUtil = new MineTemplateUtil(new File(worldFolder, mineFile.getName()));
            mineTemplateUtil.init();
            mineTemplateUtil.load();
            registeredTemplates.put(templateWorld, mineTemplateUtil);
            logger.info("Mine template registered: " + mineTemplateUtil.getName());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while registering template: " + templateFolder.getName(), e);
        }
    }

    private void delete(File file) {
        if (!file.exists())
            return;

        if (!file.isDirectory()) {
            file.delete();
            return;
        }

        for (File subFile : file.listFiles()) {
            delete(subFile);
        }

        file.delete();
    }
}
