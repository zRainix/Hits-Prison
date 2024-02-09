package de.hits.prison.mechanic.prisonPlayer.command;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.command.anno.BaseCommand;
import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.anno.SubCommand;
import de.hits.prison.command.helper.AdvancedCommand;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.entity.PlayerCurrency;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.List;

@Component
public class VulcanicAshCommand extends AdvancedCommand {

    @Autowired
    private static PlayerCurrencyDao playerCurrencyDao;

    public VulcanicAshCommand() {
        super("ash");
    }

    @BaseCommand
    public void getAsh(Player player) {
        PlayerCurrency targetAsh = playerCurrencyDao.findByPlayer(player);

        player.sendMessage("§7Ash balance: §6" + targetAsh.getVulcanicAsh() + "§7.");
    }

    @SubCommand(subCommand = "get")
    public void getTargetAsh(Player player,
                             @CommandParameter(name = "target") Player target) {

        PlayerCurrency targetAsh = playerCurrencyDao.findByPlayer(target);

        if (targetAsh == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        player.sendMessage("§7Ash balance of §6" + target.getName() + "§7: §6" + targetAsh.getVulcanicAsh() + "§7.");
    }

    @SubCommand(subCommand = "set")
    public void setTargetAsh(Player player,
                             @CommandParameter(name = "target") Player target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetAsh = playerCurrencyDao.findByPlayer(target);

        if (targetAsh == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        targetAsh.setVulcanicAsh(amount);

        playerCurrencyDao.save(targetAsh);

        player.sendMessage("§7Ash balance of §6" + target.getName() + " §7set to §6" + amount + "§7.");
    }

    @SubCommand(subCommand = "remove")
    public void removeTargetAsh(Player player,
                                @CommandParameter(name = "target") Player target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetAsh = playerCurrencyDao.findByPlayer(target);

        if (targetAsh == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        targetAsh.setVulcanicAsh(targetAsh.getVulcanicAsh().subtract(amount).min(new BigInteger("0")));

        playerCurrencyDao.save(targetAsh);

        player.sendMessage("§7Ash balance of §c" + target.getName() + " §7was removed §c" + amount + "§7. New balance: §6" + targetAsh.getVulcanicAsh() + "§7.");
    }

    @SubCommand(subCommand = "add")
    public void addTargetAsh(Player player,
                             @CommandParameter(name = "target") Player target,
                             @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetAsh = playerCurrencyDao.findByPlayer(target);

        if (targetAsh == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        targetAsh.setVulcanicAsh(targetAsh.getVulcanicAsh().add(amount));

        playerCurrencyDao.save(targetAsh);

        player.sendMessage("§7Ash balance of §6" + target.getName() + " §7was added §6" + amount + "§7. New balance: §6" + targetAsh.getVulcanicAsh() + "§7.");
    }

    @SubCommand(subCommand = "top")
    public void getTopTen(Player player) {
        List<PlayerCurrency> topVulcanicAsh = playerCurrencyDao.findTopPlayersByCategory("vulcanicAsh", 10);

        if(!topVulcanicAsh.isEmpty())  {
            player.sendMessage("§7Top §610 §7Players by §6Vulcanic Ash§7:");
            for(int i = 0; i < topVulcanicAsh.size(); i++) {
                PlayerCurrency topPlayer = topVulcanicAsh.get(i);
                player.sendMessage("§6" + (i + 1) + ". §7" + topPlayer.getRefPrisonPlayer().getPlayerName() + " - §6Ash: §a" + topPlayer.getVulcanicAsh());
            }
        } else {
            player.sendMessage("§cNo players found!");
        }
    }
}
