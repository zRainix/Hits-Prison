package de.hits.prison.prisonPlayer.command;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.anno.BaseCommand;
import de.hits.prison.base.command.anno.CommandParameter;
import de.hits.prison.base.command.anno.SubCommand;
import de.hits.prison.base.command.helper.AdvancedCommand;
import de.hits.prison.base.model.dao.PlayerCurrencyDao;
import de.hits.prison.base.model.entity.PlayerCurrency;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.model.helper.PrisonRepository;
import de.hits.prison.prisonPlayer.cache.impl.TopPlayerObsidianShardsCache;
import de.hits.prison.prisonPlayer.scheduler.TopPlayerCacheScheduler;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;

@Component
public class ObsidianShardsCommand extends AdvancedCommand {

    private static final String SHARDS_GET_PERMISSION = "prison.shards.get";
    private static final String SHARDS_MODIFY_PERMISSION = "prison.shards.modify";

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
        MessageUtil.sendMessage(player, "§7Shards balance: §6" + targetShards.formatObsidianShards() + "§7.");
    }

    @SubCommand(value = "get", permission = SHARDS_GET_PERMISSION)
    public void getTargetShards(CommandSender sender,
                                @CommandParameter(name = "target") PrisonPlayer target) {
        PlayerCurrency targetShards = target.getPlayerCurrency();
        if (targetShards == null) {
            MessageUtil.sendMessage(sender, "§cThis player does not exist!");
            return;
        }
        MessageUtil.sendMessage(sender, "§7Shards balance of §6" + target.getPlayerName() + "§7: §6" + targetShards.formatObsidianShards() + "§7.");
    }

    @SubCommand(value = "set", permission = SHARDS_MODIFY_PERMISSION)
    public void setTargetShards(CommandSender sender,
                                @CommandParameter(name = "target") PrisonPlayer target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetShards = target.getPlayerCurrency();

        if (targetShards == null) {
            MessageUtil.sendMessage(sender, "§cThis player does not exist!");
            return;
        }

        targetShards.setObsidianShards(amount.min(PrisonRepository.maxBigIntegerValue));

        playerCurrencyDao.save(targetShards);

        MessageUtil.sendMessage(sender, "§7Shards balance of §6" + target.getPlayerName() + " §7set to §6" + targetShards.formatObsidianShards() + "§7.");
    }

    @SubCommand(value = "remove", permission = SHARDS_MODIFY_PERMISSION)
    public void removeTargetShards(CommandSender sender,
                                   @CommandParameter(name = "target") PrisonPlayer target,
                                   @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetShards = target.getPlayerCurrency();

        if (targetShards == null) {
            MessageUtil.sendMessage(sender, "§cThis player does not exist!");
            return;
        }

        targetShards.setObsidianShards(targetShards.getObsidianShards().subtract(amount).max(BigInteger.ZERO));

        playerCurrencyDao.save(targetShards);

        MessageUtil.sendMessage(sender, "§7Shards balance of §6" + target.getPlayerName() + " §7was removed §6" + amount + "§7. New balance: §6" + targetShards.formatObsidianShards() + "§7.");
    }

    @SubCommand(value = "add", permission = SHARDS_MODIFY_PERMISSION)
    public void addTargetShards(CommandSender sender,
                                @CommandParameter(name = "target") PrisonPlayer target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetShards = target.getPlayerCurrency();

        if (targetShards == null) {
            MessageUtil.sendMessage(sender, "§cThis player does not exist!");
            return;
        }

        targetShards.setObsidianShards(targetShards.getObsidianShards().add(amount).min(PrisonRepository.maxBigIntegerValue));

        playerCurrencyDao.save(targetShards);

        MessageUtil.sendMessage(sender, "§7Shards balance of §6" + target.getPlayerName() + " §7was added §6" + amount + "§7. New balance: §6" + targetShards.formatObsidianShards() + "§7.");
    }

    @SubCommand("top")
    public void getTopTen(CommandSender sender) {
        List<PlayerCurrency> topObsidianShards = topPlayerObsidianShardsCache.getTopPlayerCache();

        if (!topObsidianShards.isEmpty()) {
            MessageUtil.sendMessage(sender, "§7Top §610 §7Players by §6Obsidian Shards§7:");
            for (int i = 0; i < topObsidianShards.size(); i++) {
                PlayerCurrency topPlayer = topObsidianShards.get(i);
                MessageUtil.sendMessage(sender, "§6" + (i + 1) + ". §7" + topPlayer.getRefPrisonPlayer().getPlayerName() + " - §6Shards: §a" + topPlayer.formatObsidianShards(), false);
            }
            MessageUtil.sendMessage(sender, "§7Next update in §6" + topPlayerCacheScheduler.getTimeUntilNextUpdate(), false);
        } else {
            MessageUtil.sendMessage(sender, "§cNo players found!", false);
        }
    }
}
