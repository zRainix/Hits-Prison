package de.hits.prison.mechanic.prisonPlayer.command;

import de.hits.prison.command.anno.BaseCommand;
import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.anno.SubCommand;
import de.hits.prison.command.helper.AdvancedCommand;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.entity.PlayerCurrency;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;

public class ObsidianShardsCommand extends AdvancedCommand {

    private final PlayerCurrencyDao playerCurrencyDao;

    public ObsidianShardsCommand(PlayerCurrencyDao playerCurrencyDao) {
        super("shards");
        this.playerCurrencyDao = playerCurrencyDao;
    }

    @BaseCommand
    public void getShards(Player player) {
        PlayerCurrency targetShards = this.playerCurrencyDao.findByPlayer(player);

        player.sendMessage("§7Shards balance: §6" + targetShards.getObsidianShards() + "§7.");
    }

    @SubCommand(subCommand = "get")
    public void getTargetShards(Player player,
                             @CommandParameter(name = "target") Player target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetShards = this.playerCurrencyDao.findByPlayer(target);

        if (targetShards == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        targetShards.setObsidianShards(amount);
        playerCurrencyDao.save(targetShards);

        player.sendMessage("§7Shards balance of §6" + target.getName() + " §7set to §6" + amount + "§7.");
    }

    @SubCommand(subCommand = "set")
    public void setTargetShards(Player player,
                             @CommandParameter(name = "target") Player target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetShards = this.playerCurrencyDao.findByPlayer(target);

        if (targetShards == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        targetShards.setObsidianShards(amount);

        playerCurrencyDao.save(targetShards);

        player.sendMessage("§7Shards balance of §6" + target.getName() + " §7set to §6" + amount + "§7.");
    }

    @SubCommand(subCommand = "remove")
    public void removeTargetShards(Player player,
                                @CommandParameter(name = "target") Player target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetShards = this.playerCurrencyDao.findByPlayer(target);

        if (targetShards == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        targetShards.setObsidianShards(targetShards.getObsidianShards().subtract(amount).min(new BigInteger("0")));

        playerCurrencyDao.save(targetShards);

        player.sendMessage("§7Shards balance of §c" + target.getName() + " §7was removed §c" + amount + "§7. New balance: §6" + targetShards.getObsidianShards() + "§7.");
    }

    @SubCommand(subCommand = "add")
    public void addTargetShards(Player player,
                             @CommandParameter(name = "target") Player target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetShards = this.playerCurrencyDao.findByPlayer(target);

        if (targetShards == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        targetShards.setObsidianShards(targetShards.getObsidianShards().add(amount));

        playerCurrencyDao.save(targetShards);

        player.sendMessage("§7Shards balance of §6" + target.getName() + " §7was added §6" + amount + "§7. New balance: §6" + targetShards.getObsidianShards() + "§7.");
    }

    @SubCommand(subCommand = "top")
    public void getTopTen(Player player) {
        List<PlayerCurrency> topObsidianShards = this.playerCurrencyDao.findTopPlayersByCategory("obsidianShards", 10);

        if(!topObsidianShards.isEmpty())  {
            player.sendMessage("§7Top §610 §7Players by §6Obsidian Shards§7:");
            for(int i = 0; i < topObsidianShards.size(); i++) {
                PlayerCurrency topPlayer = topObsidianShards.get(i);
                player.sendMessage("§6" + (i + 1) + ". §7" + topPlayer.getRefPrisonPlayer().getPlayerName() + " - §6Shards: §a" + topPlayer.getObsidianShards());
            }
        } else {
            player.sendMessage("§cNo players found!");
        }
    }
}
