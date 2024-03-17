package de.hits.prison.server.placeholder;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.fileUtil.helper.AnimateLineFileUtil;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.scoreboard.fileUtil.ScoreboardUtil;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class PlayerScoreboard {

    @Autowired
    private static ScoreboardUtil scoreboardUtil;
    @Autowired
    private static PlaceholderHelper placeholderHelper;

    PrisonPlayer prisonPlayer;
    ScoreboardUtil.PrisonScoreboard prisonScoreboard;
    Scoreboard scoreboard;

    Map<Team, AnimateLineFileUtil.AnimatedLines> replacements;

    public PlayerScoreboard(PrisonPlayer prisonPlayer, ScoreboardUtil.PrisonScoreboard prisonScoreboard) {
        this.prisonPlayer = prisonPlayer;
        this.prisonScoreboard = prisonScoreboard;
        this.scoreboard = null;
        this.replacements = new HashMap<>();
        init();
    }

    public void reload() {
        if (prisonScoreboard == null)
            return;

        prisonScoreboard = scoreboardUtil.getPrisonScoreboard(prisonScoreboard.getName());

        init();
    }

    public void init() {
        this.scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        if (prisonScoreboard == null)
            return;
        for (Objective objective : scoreboard.getObjectives()) {
            objective.unregister();
        }
        StrSubstitutor strSubstitutor = placeholderHelper.getPlayerPlaceholderSubstitutor(prisonPlayer.getOfflinePlayer());

        Objective objective = scoreboard.registerNewObjective(prisonScoreboard.getName(), Criteria.DUMMY, strSubstitutor.replace(prisonScoreboard.getDisplayName()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        List<AnimateLineFileUtil.AnimatedLines> rows = prisonScoreboard.getRows();

        replacements.clear();

        for (int i = 0; i < rows.size(); i++) {
            AnimateLineFileUtil.AnimatedLines animatedLines = rows.get(i);
            AnimateLineFileUtil.AnimatedLine animatedLine = animatedLines.getCurrentLine();
            String row = animatedLine.getText();
            String emptyString = "§a".repeat(i);
            row += emptyString;
            String replacedRow = strSubstitutor.replace(row);
            int score = rows.size() - i;
            if (row.equals(replacedRow)) {
                objective.getScore(replacedRow).setScore(score);
            } else {
                Team team = scoreboard.registerNewTeam("row" + i);
                team.addEntry(emptyString);
                team.setPrefix(replacedRow);
                objective.getScore(emptyString).setScore(score);
                replacements.put(team, animatedLines);
            }
        }
    }

    public PrisonPlayer getPrisonPlayer() {
        return prisonPlayer;
    }

    public ScoreboardUtil.PrisonScoreboard getPrisonScoreboard() {
        return prisonScoreboard;
    }

    public void update() {
        for (Map.Entry<Team, AnimateLineFileUtil.AnimatedLines> entry : replacements.entrySet()) {
            String string = entry.getValue().getCurrentLine().getText();
            StrSubstitutor strSubstitutor = placeholderHelper.getPlayerPlaceholderSubstitutor(prisonPlayer.getOfflinePlayer(), string);
            entry.getKey().setPrefix(strSubstitutor.replace(string));
        }
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
