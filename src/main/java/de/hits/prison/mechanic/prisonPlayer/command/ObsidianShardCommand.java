package de.hits.prison.mechanic.prisonPlayer.command;

import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.helper.SimpleCommand;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.entity.PlayerCurrency;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;

public class ObsidianShardCommand extends SimpleCommand {

    private final PlayerCurrencyDao playerCurrencyDao;

    public ObsidianShardCommand(PlayerCurrencyDao playerCurrencyDao) {
        super("shards");
        this.playerCurrencyDao = playerCurrencyDao;
    }

    public void execute(Player sender,
                          @CommandParameter(name = "subCommand", required = false) String subCommand,
                          @CommandParameter(name = "target", required = false) Player target,
                          @CommandParameter(name = "amount", required = false) BigInteger amount) {

        if(target == null) {
            target = sender;
        }

        PlayerCurrency targetShards = this.playerCurrencyDao.findByPlayer(target);
        Player player = (Player) sender;

        if(targetShards == null) {
            player.sendMessage("§7This player does not exist!");
            return;
        }

        if(subCommand == null || subCommand.isEmpty()) {
            player.sendMessage("§7shards balance: §c" + targetShards.getObsidianShards());
        } else {
            switch(subCommand.toLowerCase()) {

                case "set":
                    if(amount != null) {
                        targetShards.setObsidianShards(amount);
                        playerCurrencyDao.save(targetShards);
                        player.sendMessage("§7shards balance of §c" + target.getName() + " §7set to §c" + amount);
                    } else {
                        player.sendMessage("Usage: /shards set <Player> <amount>");
                    }
                    break;

                case "add":
                    if(amount != null) {
                        targetShards.setObsidianShards(targetShards.getObsidianShards().add(amount));
                        playerCurrencyDao.save(targetShards);
                        player.sendMessage("§7shards balance of §c" + target.getName() + " §7was added §c" + amount);
                    } else {
                        player.sendMessage("Usage: /shards add <Player> <amount>");
                    }
                    break;

                case "remove":
                    if(amount != null) {
                        targetShards.setObsidianShards(targetShards.getObsidianShards().subtract(amount));
                        playerCurrencyDao.save(targetShards);
                        player.sendMessage("§7shards balance of §c" + target.getName() + " §7was removed §c" + amount);
                    } else {
                        player.sendMessage("Usage: /shards remove <Player> <amount>");
                    }
                    break;

                case "top":
                    List<PlayerCurrency> topEXPPlayers = playerCurrencyDao.findTopPlayersByCategory("obsidianShards", 10);

                    if(!topEXPPlayers.isEmpty())  {
                        player.sendMessage("§cTop 10 Players by Obsidian Shards");
                        for(int i = 0; i < topEXPPlayers.size(); i++) {
                            PlayerCurrency topPlayer = topEXPPlayers.get(i);
                            player.sendMessage("§c" + (i + 1) + ". " + topPlayer.getRefPrisonPlayer().getPlayerName() + " - Shards: " + topPlayer.getObsidianShards());
                        }
                    } else {
                        player.sendMessage("§cNo more players found!");
                    }
                    break;

                default:
                    Player specifiedPlayer = Bukkit.getPlayer(subCommand);
                    if (specifiedPlayer != null) {
                        PlayerCurrency specifiedPlayershards = this.playerCurrencyDao.findByPlayer(specifiedPlayer);
                        player.sendMessage("§7shards balance of §c" + specifiedPlayer.getName() + ": §c" + specifiedPlayershards.getObsidianShards());
                    } else {

                        player.sendMessage("§cUnknown subCommand: " + subCommand + " §crefer to standards <set|add|remove>");
                    }
                    break;
            }
        }
    }
}
