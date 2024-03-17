package de.hits.prison.playerList.fileUtil;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.AnimateLineFileUtil;
import de.hits.prison.playerList.scheduler.PlayerListScheduler;

import java.util.ArrayList;
import java.util.List;

@Component
@SettingsFile
public class PlayerListUtil extends AnimateLineFileUtil {

    @Autowired
    private static PlayerListScheduler playerListScheduler;

    long updatePeriod;
    List<AnimatedLines> header;
    List<AnimatedLines> footer;

    private final List<AnimatedLines> defaultHeader = List.of(
            new AnimatedLines(0, List.of(new AnimatedLine("${server.prefix}"))),
            new AnimatedLines(0, List.of(new AnimatedLine(""))));
    private final List<AnimatedLines> defaultFooter = List.of(
            new AnimatedLines(0, List.of(new AnimatedLine(""))),
            new AnimatedLines(0, List.of(new AnimatedLine("${server.pc}${server.tps}"))));


    public PlayerListUtil() {
        super("tabList.yml");
        this.updatePeriod = 5L;
        this.header = new ArrayList<>();
        this.footer = new ArrayList<>();
    }

    public List<AnimatedLines> getHeader() {
        return header;
    }

    public List<AnimatedLines> getFooter() {
        return footer;
    }

    public long getUpdatePeriod() {
        return updatePeriod;
    }

    @Override
    public void init() {
        cfg.addDefault("UpdatePeriod", updatePeriod);
        setAnimatedLinesList("Header", defaultHeader, true);
        setAnimatedLinesList("Footer", defaultFooter, true);
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("UpdatePeriod", updatePeriod);
        setAnimatedLinesList("Header", header);
        setAnimatedLinesList("Footer", footer);
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();
        if (cfg.contains("UpdatePeriod"))
            updatePeriod = cfg.getLong("UpdatePeriod");
        if (cfg.contains("Header"))
            header = getAnimatedLinesList("Header");
        if (cfg.contains("Footer"))
            footer = getAnimatedLinesList("Footer");

        restartScheduler();
    }

    private void restartScheduler() {
        if (playerListScheduler == null)
            return;

        if (!playerListScheduler.isRunning())
            return;

        playerListScheduler.stop();
        playerListScheduler.setDelay(0);
        playerListScheduler.setPeriod(this.updatePeriod);
        playerListScheduler.start();
    }
}
