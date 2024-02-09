package de.hits.prison.mechanic.prisonPlayer.command;

import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.helper.SimpleCommand;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.entity.PlayerCurrency;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;

public class EXPCommand extends SimpleCommand {

    private final PlayerCurrencyDao playerCurrencyDao;

    public EXPCommand(PlayerCurrencyDao playerCurrencyDao) {
        super("exp");
        this.playerCurrencyDao = playerCurrencyDao;
    }

    public void execute(Player sender,
                          @CommandParameter(name = "subCommand", required = false) String subCommand,
                          @CommandParameter(name = "target", required = false) Player target,
                          @CommandParameter(name = "amount", required = false) BigInteger amount) {

        if(target == null) {
            target = sender;
        }

        PlayerCurrency targetEXP = this.playerCurrencyDao.findByPlayer(target);
        Player player = (Player) sender;

        if(targetEXP == null) {
            player.sendMessage("§7This player does not exist!");
            return;
        }

        if(subCommand == null || subCommand.isEmpty()) {
            player.sendMessage("§7EXP balance: §c" + targetEXP.getEXP());
        } else {
            switch(subCommand.toLowerCase()) {

                case "set":
                    if(amount != null) {
                        targetEXP.setEXP(amount);
                        playerCurrencyDao.save(targetEXP);
                        player.sendMessage("§7EXP balance of §c" + target.getName() + " §7set to §c" + amount);
                    } else {
                        player.sendMessage("Usage: /EXP set <Player> <amount>");
                    }
                    break;

                case "add":
                    if(amount != null) {
                        targetEXP.setEXP(targetEXP.getEXP().add(amount));
                        playerCurrencyDao.save(targetEXP);
                        player.sendMessage("§7EXP balance of §c" + target.getName() + " §7was added §c" + amount);
                    } else {
                        player.sendMessage("Usage: /EXP add <Player> <amount>");
                    }
                    break;

                case "remove":
                    if(amount != null) {
                        targetEXP.setEXP(targetEXP.getEXP().subtract(amount));
                        playerCurrencyDao.save(targetEXP);
                        player.sendMessage("§7EXP balance of §c" + target.getName() + " §7was removed §c" + amount);
                    } else {
                        player.sendMessage("Usage: /EXP remove <Player> <amount>");
                    }
                    break;

                case "top":
                    List<PlayerCurrency> topEXPPlayers = playerCurrencyDao.findTopPlayersByCategory("exp", 10);

                    if(!topEXPPlayers.isEmpty())  {
                        player.sendMessage("§cTop 10 Players by EXP");
                        for(int i = 0; i < topEXPPlayers.size(); i++) {
                            PlayerCurrency topPlayer = topEXPPlayers.get(i);
                            player.sendMessage("§c" + (i + 1) + ". " + topPlayer.getRefPrisonPlayer().getPlayerName() + " - EXP: " + topPlayer.getEXP());
                        }
                    } else {
                        player.sendMessage("§cNo more players found!");
                    }
                    break;

                default:
                    Player specifiedPlayer = Bukkit.getPlayer(subCommand);
                    if (specifiedPlayer != null) {
                        PlayerCurrency specifiedPlayerEXP = this.playerCurrencyDao.findByPlayer(specifiedPlayer);
                        player.sendMessage("§7EXP balance of §c" + specifiedPlayer.getName() + ": §c" + specifiedPlayerEXP.getEXP());
                    } else {

                        player.sendMessage("§cUnknown subCommand: " + subCommand + " §crefer to standards <set|add|remove>");
                    }
                    break;

            }
        }
    }
}
