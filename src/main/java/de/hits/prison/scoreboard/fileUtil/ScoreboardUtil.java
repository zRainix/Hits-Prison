package de.hits.prison.scoreboard.fileUtil;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.AnimateLineFileUtil;
import de.hits.prison.scoreboard.ScoreboardManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@SettingsFile
public class ScoreboardUtil extends AnimateLineFileUtil {

    @Autowired
    private static ScoreboardManager scoreboardManager;

    private final PrisonScoreboard defaultPrisonScoreboard = new PrisonScoreboard("Main", "§b§lPRISON", 20L, "", "§7Welcome §b${prisonPlayer.name}", "", "§6^^", "");

    List<PrisonScoreboard> prisonScoreboards;

    public ScoreboardUtil() {
        super("scoreboards.yml");
        prisonScoreboards = new ArrayList<>();
    }

    @Override
    public void init() {
        loadConfig();
        if (cfg.contains("Scoreboard"))
            return;

        String path = "Scoreboard." + defaultPrisonScoreboard.getName();
        cfg.addDefault(path + ".DisplayName", defaultPrisonScoreboard.getDisplayName());
        cfg.addDefault(path + ".UpdatePeriod", defaultPrisonScoreboard.getUpdatePeriod());
        setAnimatedLinesList(path + ".Rows", defaultPrisonScoreboard.getRows(), true);
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Scoreboard", null);
        for (PrisonScoreboard scoreboard : prisonScoreboards) {
            String path = "Scoreboard." + scoreboard.getName();
            cfg.set(path + ".DisplayName", scoreboard.getDisplayName());
            cfg.set(path + ".UpdatePeriod", scoreboard.getUpdatePeriod());
            setAnimatedLinesList(path + ".Rows", scoreboard.getRows());
        }
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();
        ConfigurationSection scoreboardSection = cfg.getConfigurationSection("Scoreboard");
        if (scoreboardSection == null)
            return;
        prisonScoreboards.clear();
        for (String name : scoreboardSection.getKeys(false)) {
            String displayName = scoreboardSection.getString(name + ".DisplayName", "Title");
            List<AnimatedLines> rows = getAnimatedLinesList("Scoreboard." + name + ".Rows");
            long updatePeriod = scoreboardSection.getLong(name + ".UpdatePeriod", 20L);
            prisonScoreboards.add(new PrisonScoreboard(name, displayName, updatePeriod, rows));
        }

        reloadScoreboards();
    }

    private void reloadScoreboards() {
        if (scoreboardManager == null)
            return;

        scoreboardManager.getScoreboardSchedulerMap().forEach((string, scoreboardScheduler) -> {
            scoreboardScheduler.update(getPrisonScoreboard(string));
        });
    }

    public List<PrisonScoreboard> getPrisonScoreboards() {
        return prisonScoreboards;
    }

    public PrisonScoreboard getPrisonScoreboard(String name) {
        for (PrisonScoreboard prisonScoreboard : prisonScoreboards) {
            if (prisonScoreboard.getName().equals(name))
                return prisonScoreboard;
        }
        return null;
    }

    public PrisonScoreboard getMainPrisonScoreboard() {
        PrisonScoreboard prisonScoreboard = getPrisonScoreboard("Main");
        if (prisonScoreboard != null)
            return prisonScoreboard;
        return getDefaultPrisonScoreboard();
    }

    public PrisonScoreboard getDefaultPrisonScoreboard() {
        return defaultPrisonScoreboard;
    }

    public static class PrisonScoreboard {

        String name;
        String displayName;
        long updatePeriod;
        List<AnimatedLines> rows;

        public PrisonScoreboard(String name, String displayName, long updatePeriod, String... rows) {
            this.name = name;
            this.displayName = displayName;
            this.updatePeriod = updatePeriod;
            this.rows = Arrays.stream(rows).map(line -> new AnimatedLines(0, List.of(new AnimatedLine(line)))).collect(Collectors.toList());
        }

        public PrisonScoreboard(String name, String displayName, long updatePeriod, List<AnimatedLines> rows) {
            this.name = name;
            this.displayName = displayName;
            this.updatePeriod = updatePeriod;
            this.rows = rows;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public long getUpdatePeriod() {
            return updatePeriod;
        }

        public void setUpdatePeriod(long updatePeriod) {
            this.updatePeriod = updatePeriod;
        }

        public List<AnimatedLines> getRows() {
            return rows;
        }

        public void setRows(List<AnimatedLines> rows) {
            this.rows = rows;
        }
    }
}
