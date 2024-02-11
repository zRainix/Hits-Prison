package de.hits.prison.mechanic.prisonPlayer.command;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.command.anno.BaseCommand;
import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.anno.SubCommand;
import de.hits.prison.command.helper.AdvancedCommand;
import de.hits.prison.mechanic.prisonPlayer.cache.impl.TopPlayerObsidianShardsCache;
import de.hits.prison.mechanic.prisonPlayer.scheduler.TopPlayerCacheScheduler;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.entity.PlayerCurrency;
import de.hits.prison.model.entity.PrisonPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;

@Component
public class ObsidianShardsCommand extends AdvancedCommand {

    @Autowired
    private static PlayerCurrencyDao playerCurrencyDao;
    @Autowired
    private static TopPlayerObsidianShardsCache topPlayerObsidianShardsCache;
    @Autowired
    private static TopPlayerCacheScheduler topPlayerCacheScheduler;

    public ObsidianShardsCommand() {
        super("shards");
    }

    @BaseCommand
    public void getShards(Player player) {
        PlayerCurrency targetShards = playerCurrencyDao.findByPlayer(player);
        player.sendMessage("§7Shards balance: §6" + targetShards.formatObsidianShards() + "§7.");
    }

    @SubCommand(subCommand = "get")
    public void getTargetShards(CommandSender sender,
                                @CommandParameter(name = "target") PrisonPlayer target) {
        PlayerCurrency targetShards = target.getPlayerCurrency();
        if (targetShards == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }
        sender.sendMessage("§7Shards balance of §6" + target.getPlayerName() + "§7: §6" + targetShards.formatObsidianShards() + "§7.");
    }

    @SubCommand(subCommand = "set")
    public void setTargetShards(CommandSender sender,
                                @CommandParameter(name = "target") PrisonPlayer target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetShards = target.getPlayerCurrency();

        if (targetShards == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }

        targetShards.setObsidianShards(amount);

        playerCurrencyDao.save(targetShards);

        sender.sendMessage("§7Shards balance of §6" + target.getPlayerName() + " §7set to §6" + targetShards.formatObsidianShards() + "§7.");
    }

    @SubCommand(subCommand = "remove")
    public void removeTargetShards(CommandSender sender,
                                   @CommandParameter(name = "target") PrisonPlayer target,
                                   @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetShards = target.getPlayerCurrency();

        if (targetShards == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }

        targetShards.setObsidianShards(targetShards.getObsidianShards().subtract(amount).min(BigInteger.ZERO));

        playerCurrencyDao.save(targetShards);

        sender.sendMessage("§7Shards balance of §c" + target.getPlayerName() + " §7was removed §c" + amount + "§7. New balance: §6" + targetShards.formatObsidianShards() + "§7.");
    }

    @SubCommand(subCommand = "add")
    public void addTargetShards(CommandSender sender,
                                @CommandParameter(name = "target") PrisonPlayer target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetShards = target.getPlayerCurrency();

        if (targetShards == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }

        targetShards.setObsidianShards(targetShards.getObsidianShards().add(amount));

        playerCurrencyDao.save(targetShards);

        sender.sendMessage("§7Shards balance of §6" + target.getPlayerName() + " §7was added §6" + amount + "§7. New balance: §6" + targetShards.formatObsidianShards() + "§7.");
    }

    @SubCommand(subCommand = "top")
    public void getTopTen(CommandSender sender) {
        List<PlayerCurrency> topObsidianShards = topPlayerObsidianShardsCache.getTopPlayerCache();

        if (!topObsidianShards.isEmpty()) {
            sender.sendMessage("§7Top §610 §7Players by §6Obsidian Shards§7:");
            for (int i = 0; i < topObsidianShards.size(); i++) {
                PlayerCurrency topPlayer = topObsidianShards.get(i);
                sender.sendMessage("§6" + (i + 1) + ". §7" + topPlayer.getRefPrisonPlayer().getPlayerName() + " - §6Shards: §a" + topPlayer.formatObsidianShards());
            }
            sender.sendMessage("§7Next update in §6" + topPlayerCacheScheduler.getTimeUntilNextUpdate());
        } else {
            sender.sendMessage("§cNo players found!");
        }
    }
}
