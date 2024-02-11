package de.hits.prison.mechanic.prisonPlayer.command;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.command.anno.BaseCommand;
import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.anno.SubCommand;
import de.hits.prison.command.helper.AdvancedCommand;
import de.hits.prison.mechanic.prisonPlayer.cache.impl.TopPlayerExpCache;
import de.hits.prison.mechanic.prisonPlayer.scheduler.TopPlayerCacheScheduler;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.entity.PlayerCurrency;
import de.hits.prison.model.entity.PrisonPlayer;
import de.hits.prison.model.helper.PrisonRepository;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;

@Component
public class ExpCommand extends AdvancedCommand {

    @Autowired
    private static PlayerCurrencyDao playerCurrencyDao;
    @Autowired
    private static TopPlayerExpCache topPlayerExpCache;
    @Autowired
    private static TopPlayerCacheScheduler topPlayerCacheScheduler;

    public ExpCommand() {
        super("exp");
    }

    @BaseCommand
    public void getExp(Player player) {
        PlayerCurrency targetExp = playerCurrencyDao.findByPlayer(player);
        player.sendMessage("§7Exp balance: §6" + targetExp.formatExp() + "§7.");
    }

    @SubCommand(subCommand = "get")
    public void getTargetExp(CommandSender sender,
                             @CommandParameter(name = "target") PrisonPlayer target) {
        PlayerCurrency targetExp = target.getPlayerCurrency();
        if (targetExp == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }
        sender.sendMessage("§7Exp balance of §6" + target.getPlayerName() + "§7: §6" + targetExp.formatExp() + "§7.");
    }

    @SubCommand(subCommand = "set")
    public void setTargetExp(CommandSender sender,
                             @CommandParameter(name = "target") PrisonPlayer target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetExp = target.getPlayerCurrency();

        if (targetExp == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }

        targetExp.setExp(amount.min(PrisonRepository.maxBigIntegerValue));

        playerCurrencyDao.save(targetExp);

        sender.sendMessage("§7Exp balance of §6" + target.getPlayerName() + " §7set to §6" + targetExp.formatExp() + "§7.");
    }

    @SubCommand(subCommand = "remove")
    public void removeTargetExp(CommandSender sender,
                                @CommandParameter(name = "target") PrisonPlayer target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetExp = target.getPlayerCurrency();

        if (targetExp == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }

        targetExp.setExp(targetExp.getExp().subtract(amount).max(BigInteger.ZERO));

        playerCurrencyDao.save(targetExp);

        sender.sendMessage("§7Exp balance of §6" + target.getPlayerName() + " §7was removed §6" + amount + "§7. New balance: §6" + targetExp.formatExp() + "§7.");
    }

    @SubCommand(subCommand = "add")
    public void addTargetExp(CommandSender sender,
                             @CommandParameter(name = "target") PrisonPlayer target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetExp = target.getPlayerCurrency();

        if (targetExp == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }

        targetExp.setExp(targetExp.getExp().add(amount).min(PrisonRepository.maxBigIntegerValue));

        playerCurrencyDao.save(targetExp);

        sender.sendMessage("§7Exp balance of §6" + target.getPlayerName() + " §7was added §6" + amount + "§7. New balance: §6" + targetExp.formatExp() + "§7.");
    }

    @SubCommand(subCommand = "top")
    public void getTopTen(CommandSender sender) {
        List<PlayerCurrency> topExp = topPlayerExpCache.getTopPlayerCache();

        if (!topExp.isEmpty()) {
            sender.sendMessage("§7Top §610 §7Players by §6Exp§7:");
            for (int i = 0; i < topExp.size(); i++) {
                PlayerCurrency topPlayer = topExp.get(i);
                sender.sendMessage("§6" + (i + 1) + ". §7" + topPlayer.getRefPrisonPlayer().getPlayerName() + " - §6Exp: §a" + topPlayer.formatExp());
            }
            sender.sendMessage("§7Next update in §6" + topPlayerCacheScheduler.getTimeUntilNextUpdate());
        } else {
            sender.sendMessage("§cNo players found!");
        }
    }
}
