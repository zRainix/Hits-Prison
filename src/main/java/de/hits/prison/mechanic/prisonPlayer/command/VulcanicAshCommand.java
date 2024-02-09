package de.hits.prison.mechanic.prisonPlayer.command;

import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.helper.SimpleCommand;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.entity.PlayerCurrency;
import org.bukkit.entity.Player;

import java.math.BigInteger;

public class VulcanicAshCommand extends SimpleCommand {

    private final PlayerCurrencyDao playerCurrencyDao;

    public VulcanicAshCommand(PlayerCurrencyDao playerCurrencyDao) {
        super("ash");
        this.playerCurrencyDao = playerCurrencyDao;
    }

    public void onCommand(Player sender,
                          @CommandParameter(name = "target", required = false) Player target,
                          @CommandParameter(name = "subCommand", required = false) String subCommand,
                          @CommandParameter(name = "amount", required = false) BigInteger amount) {

        if(target == null) {
            target = sender;
        }

        PlayerCurrency targetAsh = this.playerCurrencyDao.findByPlayer(target);
        Player player = (Player) sender;

        if(targetAsh == null) {
            player.sendMessage("§7This player does not exist!");
            return;
        }

        if(subCommand == null || subCommand.isEmpty()) {
            player.sendMessage("§7Ash balance: §c" + targetAsh.getVulcanicAsh());
        } else {
            switch(subCommand.toLowerCase()) {

                case "set":
                    if(amount != null) {
                        targetAsh.setVulcanicAsh(amount);
                        playerCurrencyDao.save(targetAsh);
                        player.sendMessage("§7Ash balance of §c" + target.getName() + " §7set to §c" + amount);
                    } else {
                        player.sendMessage("Usage: /ash set <Player> <amount>");
                    }
                    break;

                case "add":
                    if(amount != null) {
                        targetAsh.setVulcanicAsh(targetAsh.getVulcanicAsh().add(amount));
                        playerCurrencyDao.save(targetAsh);
                        player.sendMessage("§7Ash balance of §c" + target + " §7was added §c" + amount);
                    } else {
                        player.sendMessage("Usage: /ash add <Player> <amount>");
                    }
                    break;

                case "remove":
                    if(amount != null) {
                        targetAsh.setVulcanicAsh(targetAsh.getVulcanicAsh().subtract(amount));
                        playerCurrencyDao.save(targetAsh);
                        player.sendMessage("§7Ash balance of §c" + target + " §7was removed §c" + amount);
                    } else {
                        player.sendMessage("Usage: /ash remove <Player> <amount>");
                    }
                    break;
            }
        }
    }
}
