package de.hits.prison.mine.fileUtil;

import de.hits.prison.base.fileUtil.helper.FileUtil;
import org.bukkit.Location;

import java.io.File;

public class MineTemplateUtil extends FileUtil {

    String name;
    Location centerBlock;
    Location traderLocation;
    int borderRadius;

    public MineTemplateUtil(File file) {
        super(file);
        this.name = "Template";
        this.centerBlock = new Location(null, 0, 70, 0);
        this.traderLocation = new Location(null, 100, 80, 0);
        this.borderRadius = 200;
    }

    @Override
    public void init() {
        cfg.addDefault("Name", name);
        cfg.addDefault("CenterBlock.X", centerBlock.getBlockX());
        cfg.addDefault("CenterBlock.Y", centerBlock.getBlockY());
        cfg.addDefault("CenterBlock.Z", centerBlock.getBlockZ());
        cfg.addDefault("Trader.X", traderLocation.getX());
        cfg.addDefault("Trader.Y", traderLocation.getY());
        cfg.addDefault("Trader.Z", traderLocation.getZ());
        cfg.addDefault("Trader.Yaw", traderLocation.getYaw());
        cfg.addDefault("Trader.Pitch", traderLocation.getPitch());
        cfg.addDefault("Border.Radius", borderRadius);
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Name", name);
        cfg.set("CenterBlock.X", centerBlock.getBlockX());
        cfg.set("CenterBlock.Y", centerBlock.getBlockY());
        cfg.set("CenterBlock.Z", centerBlock.getBlockZ());
        cfg.set("Trader.X", traderLocation.getX());
        cfg.set("Trader.Y", traderLocation.getY());
        cfg.set("Trader.Z", traderLocation.getZ());
        cfg.set("Trader.Yaw", traderLocation.getYaw());
        cfg.set("Trader.Pitch", traderLocation.getPitch());
        cfg.set("Border.Radius", borderRadius);
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();
        this.name = cfg.getString("Name", this.name);
        int centerX = cfg.getInt("CenterBlock.X", this.centerBlock.getBlockX());
        int centerY = cfg.getInt("CenterBlock.Y", this.centerBlock.getBlockY());
        int centerZ = cfg.getInt("CenterBlock.Z", this.centerBlock.getBlockZ());
        this.centerBlock = new Location(null, centerX, centerY, centerZ);
        double traderX = cfg.getDouble("Trader.X", this.traderLocation.getX());
        double traderY = cfg.getDouble("Trader.Y", this.traderLocation.getY());
        double traderZ = cfg.getDouble("Trader.Z", this.traderLocation.getZ());
        float traderYaw = (float) cfg.getDouble("Trader.Yaw", this.traderLocation.getYaw());
        float traderPitch = (float) cfg.getDouble("Trader.Pitch", this.traderLocation.getPitch());
        this.traderLocation = new Location(null, traderX, traderY, traderZ, traderYaw, traderPitch);
        this.borderRadius = cfg.getInt("Border.Radius", this.borderRadius);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getCenterBlock() {
        return centerBlock;
    }

    public void setCenterBlock(Location centerBlock) {
        this.centerBlock = centerBlock;
    }

    public Location getTraderLocation() {
        return traderLocation;
    }

    public void setTraderLocation(Location traderLocation) {
        this.traderLocation = traderLocation;
    }

    public int getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
    }

    public String getWorldName() {
        return this.file.getParentFile().getName();
    }
}
