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
import de.hits.prison.prisonPlayer.cache.impl.TopPlayerExpCache;
import de.hits.prison.prisonPlayer.scheduler.TopPlayerCacheScheduler;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;

@Component
public class ExpCommand extends AdvancedCommand {

    private static final String EXP_GET_PERMISSION = "prison.exp.get";
    private static final String EXP_MODIFY_PERMISSION = "prison.exp.modify";

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
        MessageUtil.sendMessage(player, "§7Exp balance: §6" + targetExp.formatExp() + "§7.");
    }

    @SubCommand(value = "get", permission = EXP_GET_PERMISSION)
    public void getTargetExp(CommandSender sender,
                             @CommandParameter(name = "target") PrisonPlayer target) {
        PlayerCurrency targetExp = target.getPlayerCurrency();
        if (targetExp == null) {
            MessageUtil.sendMessage(sender, "§cThis player does not exist!");
            return;
        }
        MessageUtil.sendMessage(sender, "§7Exp balance of §6" + target.getPlayerName() + "§7: §6" + targetExp.formatExp() + "§7.");
    }

    @SubCommand(value = "set", permission = EXP_MODIFY_PERMISSION)
    public void setTargetExp(CommandSender sender,
                             @CommandParameter(name = "target") PrisonPlayer target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetExp = target.getPlayerCurrency();

        if (targetExp == null) {
            MessageUtil.sendMessage(sender, "§cThis player does not exist!");
            return;
        }

        targetExp.setExp(amount.min(PrisonRepository.maxBigIntegerValue));

        playerCurrencyDao.save(targetExp);

        MessageUtil.sendMessage(sender, "§7Exp balance of §6" + target.getPlayerName() + " §7set to §6" + targetExp.formatExp() + "§7.");
    }

    @SubCommand(value = "remove", permission = EXP_MODIFY_PERMISSION)
    public void removeTargetExp(CommandSender sender,
                                @CommandParameter(name = "target") PrisonPlayer target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetExp = target.getPlayerCurrency();

        if (targetExp == null) {
            MessageUtil.sendMessage(sender, "§cThis player does not exist!");
            return;
        }

        targetExp.setExp(targetExp.getExp().subtract(amount).max(BigInteger.ZERO));

        playerCurrencyDao.save(targetExp);

        MessageUtil.sendMessage(sender, "§7Exp balance of §6" + target.getPlayerName() + " §7was removed §6" + amount + "§7. New balance: §6" + targetExp.formatExp() + "§7.");
    }

    @SubCommand(value = "add", permission = EXP_MODIFY_PERMISSION)
    public void addTargetExp(CommandSender sender,
                             @CommandParameter(name = "target") PrisonPlayer target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetExp = target.getPlayerCurrency();

        if (targetExp == null) {
            MessageUtil.sendMessage(sender, "§cThis player does not exist!");
            return;
        }

        targetExp.setExp(targetExp.getExp().add(amount).min(PrisonRepository.maxBigIntegerValue));

        playerCurrencyDao.save(targetExp);

        MessageUtil.sendMessage(sender, "§7Exp balance of §6" + target.getPlayerName() + " §7was added §6" + amount + "§7. New balance: §6" + targetExp.formatExp() + "§7.");
    }

    @SubCommand("top")
    public void getTopTen(CommandSender sender) {
        List<PlayerCurrency> topExp = topPlayerExpCache.getTopPlayerCache();

        if (!topExp.isEmpty()) {
            MessageUtil.sendMessage(sender, "§7Top §610 §7Players by §6Exp§7:");
            for (int i = 0; i < topExp.size(); i++) {
                PlayerCurrency topPlayer = topExp.get(i);
                MessageUtil.sendMessage(sender, "§6" + (i + 1) + ". §7" + topPlayer.getRefPrisonPlayer().getPlayerName() + " - §6Exp: §a" + topPlayer.formatExp(), false);
            }
            MessageUtil.sendMessage(sender, "§7Next update in §6" + topPlayerCacheScheduler.getTimeUntilNextUpdate(), false);
        } else {
            MessageUtil.sendMessage(sender, "§cNo players found!", false);
        }
    }
}
