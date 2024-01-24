package de.hits.customhoe;

import de.hits.util.FileUtil;
import de.hits.util.FileUtilManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomHoe extends JavaPlugin {

    FileUtilManager fileUtilManager = new FileUtilManager("plugins/CustomHoe/settings.properties");
    FileUtil fileUtil = fileUtilManager.getFileUtil();

    @Override
    public void onEnable() {
        System.out.println("PLUGIN - §aSTARTED");

        fileUtilManager.start();
        fileUtilManager.save();


        System.out.println("Key 1: " + fileUtil.getKey("key1"));
        System.out.println("Key 2: " + fileUtil.getKey("key2"));
        System.out.println("Key 3: " + fileUtil.getKey("key3"));
    }

    @Override
    public void onDisable() {
        System.out.println("PLUGIN - §cSTOPPED");

        fileUtilManager.save();
    }
}
