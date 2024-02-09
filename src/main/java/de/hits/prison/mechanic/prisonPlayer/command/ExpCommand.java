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
public class ExpCommand extends AdvancedCommand {

    @Autowired
    private static PlayerCurrencyDao playerCurrencyDao;

    public ExpCommand() {
        super("exp");
    }

    @BaseCommand
    public void getExp(Player player) {
        PlayerCurrency targetExp = playerCurrencyDao.findByPlayer(player);

        player.sendMessage("§7Exp balance: §6" + targetExp.getExp() + "§7.");
    }

    @SubCommand(subCommand = "get")
    public void getTargetExp(Player player,
                                @CommandParameter(name = "target") Player target) {
        PlayerCurrency targetExp = playerCurrencyDao.findByPlayer(target);

        if (targetExp == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        player.sendMessage("§7Exp balance of §6" + target.getName() + "§7: §6" + targetExp.getExp() + "§7.");
    }

    @SubCommand(subCommand = "set")
    public void setTargetExp(Player player,
                                @CommandParameter(name = "target") Player target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetExp = playerCurrencyDao.findByPlayer(target);

        if (targetExp == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        targetExp.setExp(amount);

        playerCurrencyDao.save(targetExp);

        player.sendMessage("§7Exp balance of §6" + target.getName() + " §7set to §6" + amount + "§7.");
    }

    @SubCommand(subCommand = "remove")
    public void removeTargetExp(Player player,
                                   @CommandParameter(name = "target") Player target,
                                   @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetExp = playerCurrencyDao.findByPlayer(target);

        if (targetExp == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        targetExp.setExp(targetExp.getExp().subtract(amount).min(new BigInteger("0")));

        playerCurrencyDao.save(targetExp);

        player.sendMessage("§7Exp balance of §c" + target.getName() + " §7was removed §c" + amount + "§7. New balance: §6" + targetExp.getExp() + "§7.");
    }

    @SubCommand(subCommand = "add")
    public void addTargetExp(Player player,
                                @CommandParameter(name = "target") Player target,
                                @CommandParameter(name = "amount") BigInteger amount) {

        PlayerCurrency targetExp = playerCurrencyDao.findByPlayer(target);

        if (targetExp == null) {
            player.sendMessage("§cThis player does not exist!");
            return;
        }

        targetExp.setExp(targetExp.getExp().add(amount));

        playerCurrencyDao.save(targetExp);

        player.sendMessage("§7Exp balance of §6" + target.getName() + " §7was added §6" + amount + "§7. New balance: §6" + targetExp.getExp() + "§7.");
    }

    @SubCommand(subCommand = "top")
    public void getTopTen(Player player) {
        List<PlayerCurrency> topExp = playerCurrencyDao.findTopPlayersByCategory("exp", 10);

        if(!topExp.isEmpty())  {
            player.sendMessage("§7Top §610 §7Players by §6Exp§7:");
            for(int i = 0; i < topExp.size(); i++) {
                PlayerCurrency topPlayer = topExp.get(i);
                player.sendMessage("§6" + (i + 1) + ". §7" + topPlayer.getRefPrisonPlayer().getPlayerName() + " - §6Exp: §a" + topPlayer.getExp());
            }
        } else {
            player.sendMessage("§cNo players found!");
        }
    }
}
