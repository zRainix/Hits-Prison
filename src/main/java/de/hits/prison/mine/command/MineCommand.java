package de.hits.prison.mine.command;

import de.hits.prison.base.command.anno.BaseCommand;
import de.hits.prison.base.command.helper.AdvancedCommand;
import org.bukkit.entity.Player;

public class MineCommand extends AdvancedCommand {
    public MineCommand() {
        super("mind");
    }

    @BaseCommand
    public void mine(Player player) {

    }
}
