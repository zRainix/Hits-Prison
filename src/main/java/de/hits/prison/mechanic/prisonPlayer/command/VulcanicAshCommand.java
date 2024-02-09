package de.hits.prison.mechanic.prisonPlayer.command;

import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.helper.SimpleCommand;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.entity.PlayerCurrency;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;

public class VulcanicAshCommand extends SimpleCommand {

    private final PlayerCurrencyDao playerCurrencyDao;

    public VulcanicAshCommand(PlayerCurrencyDao playerCurrencyDao) {
        super("ash");
        this.playerCurrencyDao = playerCurrencyDao;
    }

    public void execute(Player sender,
                          @CommandParameter(name = "subCommand", required = false) String subCommand,
                          @CommandParameter(name = "target", required = false) Player target,
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
                        player.sendMessage("§7Ash balance of §c" + target.getName() + " §7was added §c" + amount);
                    } else {
                        player.sendMessage("Usage: /ash add <Player> <amount>");
                    }
                    break;

                case "remove":
                    if(amount != null) {
                        targetAsh.setVulcanicAsh(targetAsh.getVulcanicAsh().subtract(amount));
                        playerCurrencyDao.save(targetAsh);
                        player.sendMessage("§7Ash balance of §c" + target.getName() + " §7was removed §c" + amount);
                    } else {
                        player.sendMessage("Usage: /ash remove <Player> <amount>");
                    }
                    break;

                case "top":
                    List<PlayerCurrency> topEXPPlayers = playerCurrencyDao.findTopPlayersByCategory("vulcanicAsh", 10);

                    if(!topEXPPlayers.isEmpty())  {
                        player.sendMessage("§cTop 10 Players by Ash");
                        for(int i = 0; i < topEXPPlayers.size(); i++) {
                            PlayerCurrency topPlayer = topEXPPlayers.get(i);
                            player.sendMessage("§c" + (i + 1) + ". " + topPlayer.getRefPrisonPlayer().getPlayerName() + " - Ash: " + topPlayer.getVulcanicAsh());
                        }
                    } else {
                        player.sendMessage("§cNo more players found!");
                    }
                    break;

                default:
                    Player specifiedPlayer = Bukkit.getPlayer(subCommand);
                    if (specifiedPlayer != null) {
                        PlayerCurrency specifiedPlayerAsh = this.playerCurrencyDao.findByPlayer(specifiedPlayer);
                        player.sendMessage("§7Ash balance of §c" + specifiedPlayer.getName() + ": §c" + specifiedPlayerAsh.getVulcanicAsh());
                    } else {

                        player.sendMessage("§cUnknown subCommand: " + subCommand + " §crefer to standards <set|add|remove>");
                    }
                    break;
            }
        }
    }
}
