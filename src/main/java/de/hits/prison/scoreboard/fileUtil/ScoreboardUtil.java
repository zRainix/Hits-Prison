package de.hits.prison.scoreboard.fileUtil;

import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.FileUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@SettingsFile
public class ScoreboardUtil extends FileUtil {

    private final PrisonScoreboard defaultPrisonScoreboard = new PrisonScoreboard("Main", "§b§lPRISON", List.of("", "§7Welcome §b${prisonPlayer.name}", "", "§6^^", ""));

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
        cfg.addDefault(path + ".Rows", defaultPrisonScoreboard.getRows());
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Scoreboard", null);
        for (PrisonScoreboard scoreboard : prisonScoreboards) {
            String path = "Scoreboard." + scoreboard.getName();
            cfg.set(path + ".DisplayName", scoreboard.getDisplayName());
            cfg.set(path + ".Rows", scoreboard.getRows());
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
            List<String> rows = scoreboardSection.getStringList(name + ".Rows");
            prisonScoreboards.add(new PrisonScoreboard(name, displayName, rows));
        }
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
        List<String> rows;

        public PrisonScoreboard(String name, String displayName, List<String> rows) {
            this.name = name;
            this.displayName = displayName;
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

        public List<String> getRows() {
            return rows;
        }

        public void setRows(List<String> rows) {
            this.rows = rows;
        }
    }
}
