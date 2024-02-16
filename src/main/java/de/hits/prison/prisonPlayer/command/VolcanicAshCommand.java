package de.hits.prison.prisonPlayer.command;

import de.hits.prison.prisonPlayer.cache.impl.TopPlayerVolcanicAshCache;
import de.hits.prison.prisonPlayer.scheduler.TopPlayerCacheScheduler;
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;

@Component
public class VolcanicAshCommand extends AdvancedCommand {

    @Autowired
    private static PlayerCurrencyDao playerCurrencyDao;
    @Autowired
    private static TopPlayerVolcanicAshCache topPlayerVolcanicAshCache;
    @Autowired
    private static TopPlayerCacheScheduler topPlayerCacheScheduler;

    public VolcanicAshCommand() {
        super("ash");
    }

    @BaseCommand
    public void getAsh(Player player) {
        PlayerCurrency targetAsh = playerCurrencyDao.findByPlayer(player);

        player.sendMessage("§7Ash balance: §6" + targetAsh.formatVolcanicAsh() + "§7.");
    }

    @SubCommand(subCommand = "get")
    public void getTargetAsh(CommandSender sender,
                             @CommandParameter(name = "target") PrisonPlayer target) {

        PlayerCurrency targetAsh = target.getPlayerCurrency();

        if (targetAsh == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }

        sender.sendMessage("§7Ash balance of §6" + target.getPlayerName() + "§7: §6" + targetAsh.formatVolcanicAsh() + "§7.");
    }

    @SubCommand(subCommand = "set")
    public void setTargetAsh(CommandSender sender,
                             @CommandParameter(name = "target") PrisonPlayer target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetAsh = target.getPlayerCurrency();

        if (targetAsh == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }

        targetAsh.setVolcanicAsh(amount.min(PrisonRepository.maxBigIntegerValue));

        playerCurrencyDao.save(targetAsh);

        sender.sendMessage("§7Ash balance of §6" + target.getPlayerName() + " §7set to §6" + targetAsh.formatVolcanicAsh() + "§7.");
    }

    @SubCommand(subCommand = "remove")
    public void removeTargetAsh(CommandSender sender,
                                @CommandParameter(name = "target") PrisonPlayer target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetAsh = target.getPlayerCurrency();

        if (targetAsh == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }

        targetAsh.setVolcanicAsh(targetAsh.getVolcanicAsh().subtract(amount).max(BigInteger.ZERO));

        playerCurrencyDao.save(targetAsh);

        sender.sendMessage("§7Ash balance of §6" + target.getPlayerName() + " §7was removed §6" + amount + "§7. New balance: §6" + targetAsh.formatVolcanicAsh() + "§7.");
    }

    @SubCommand(subCommand = "add")
    public void addTargetAsh(CommandSender sender,
                             @CommandParameter(name = "target") PrisonPlayer target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetAsh = target.getPlayerCurrency();

        if (targetAsh == null) {
            sender.sendMessage("§cThis player does not exist!");
            return;
        }

        targetAsh.setVolcanicAsh(targetAsh.getVolcanicAsh().add(amount).min(PrisonRepository.maxBigIntegerValue));

        playerCurrencyDao.save(targetAsh);

        sender.sendMessage("§7Ash balance of §6" + target.getPlayerName() + " §7was added §6" + amount + "§7. New balance: §6" + targetAsh.formatVolcanicAsh() + "§7.");
    }

    @SubCommand(subCommand = "top")
    public void getTopTen(CommandSender sender) {
        List<PlayerCurrency> topVolcanicAsh = topPlayerVolcanicAshCache.getTopPlayerCache();

        if (!topVolcanicAsh.isEmpty()) {
            sender.sendMessage("§7Top §610 §7Players by §6Volcanic Ash§7:");
            for (int i = 0; i < topVolcanicAsh.size(); i++) {
                PlayerCurrency topPlayer = topVolcanicAsh.get(i);
                sender.sendMessage("§6" + (i + 1) + ". §7" + topPlayer.getRefPrisonPlayer().getPlayerName() + " - §6Ash: §a" + topPlayer.formatVolcanicAsh());
            }
            sender.sendMessage("§7Next update in §6" + topPlayerCacheScheduler.getTimeUntilNextUpdate());
        } else {
            sender.sendMessage("§cNo players found!");
        }
    }
}
