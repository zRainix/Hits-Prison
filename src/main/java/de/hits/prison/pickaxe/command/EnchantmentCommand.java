package de.hits.prison.pickaxe.command;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.anno.BaseCommand;
import de.hits.prison.base.command.anno.CommandParameter;
import de.hits.prison.base.command.anno.IntParameter;
import de.hits.prison.base.command.anno.SubCommand;
import de.hits.prison.base.command.helper.AdvancedCommand;
import de.hits.prison.base.command.helper.NumberLimit;
import de.hits.prison.base.model.dao.PlayerEnchantmentDao;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.screen.ScreenManager;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.pickaxe.screen.PickaxeScreen;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Component
public class EnchantmentCommand extends AdvancedCommand {

    @Autowired
    private static PlayerEnchantmentDao playerEnchantmentDao;
    @Autowired
    private static PickaxeUtil pickaxeUtil;
    @Autowired
    private static ScreenManager screenManager;
    @Autowired
    private static PickaxeHelper pickaxeHelper;

    public EnchantmentCommand() {
        super("enchantment");
    }

    @BaseCommand
    public void testScreen(Player player) {
        screenManager.openScreen(player, new PickaxeScreen(player));
    }

    @SubCommand(subCommand = "set")
    public void setEnchantment(CommandSender sender,
                               @CommandParameter(name = "player") PrisonPlayer prisonPlayer,
                               @CommandParameter(name = "enchantment") PickaxeUtil.PickaxeEnchantment pickaxeEnchantment,
                               @CommandParameter(name = "level") @IntParameter(limit = NumberLimit.MIN) Integer level) {

        if (level == 0) {
            removeEnchantment(sender, prisonPlayer, pickaxeEnchantment);
            return;
        }

        PlayerEnchantment playerEnchantment = playerEnchantmentDao.findByPrisonPlayerAndEnchantmentName(prisonPlayer, pickaxeEnchantment.getName());
        if (playerEnchantment == null) {
            playerEnchantment = new PlayerEnchantment();
            playerEnchantment.setRefPrisonPlayer(prisonPlayer);
            playerEnchantment.setEnchantmentName(pickaxeEnchantment.getName());
        }
        playerEnchantment.setEnchantmentLevel(level);
        playerEnchantmentDao.save(playerEnchantment);

        sender.sendMessage("§aAdded enchantment §6" + pickaxeEnchantment.getName() + " (" + level + "/" + pickaxeEnchantment.getMaxLevel() + ")" + " §ato player §6" + prisonPlayer.getPlayerName() + "§a.");
        if (level > pickaxeEnchantment.getMaxLevel()) {
            sender.sendMessage("§cWarning: Level §6" + level + " §cexceeds max level: §6" + pickaxeEnchantment.getMaxLevel() + "§c.");
        }

        OfflinePlayer offlinePlayer = prisonPlayer.getOfflinePlayer();

        if (!offlinePlayer.isOnline())
            return;

        pickaxeHelper.checkPlayerInventory(offlinePlayer.getPlayer());
    }

    @SubCommand(subCommand = "remove")
    public void removeEnchantment(CommandSender sender,
                                  @CommandParameter(name = "player") PrisonPlayer prisonPlayer,
                                  @CommandParameter(name = "enchantment") PickaxeUtil.PickaxeEnchantment pickaxeEnchantment) {
        PlayerEnchantment playerEnchantment = playerEnchantmentDao.findByPrisonPlayerAndEnchantmentName(prisonPlayer, pickaxeEnchantment.getName());
        if (playerEnchantment == null) {
            sender.sendMessage("§cPlayer §6" + prisonPlayer.getPlayerName() + " §cdoes not have enchantment §6" + pickaxeEnchantment.getName() + "§c.");
            return;
        }
        playerEnchantmentDao.delete(playerEnchantment);

        sender.sendMessage("§aRemoved enchantment §6" + pickaxeEnchantment.getName() + " §afrom player §6" + prisonPlayer.getPlayerName() + "§a.");

        OfflinePlayer offlinePlayer = prisonPlayer.getOfflinePlayer();

        if (!offlinePlayer.isOnline())
            return;

        pickaxeHelper.checkPlayerInventory(offlinePlayer.getPlayer());
    }

    @SubCommand(subCommand = "list")
    public void listEnchantment(CommandSender sender,
                                @CommandParameter(name = "player") PrisonPlayer prisonPlayer) {
        List<PlayerEnchantment> playerEnchantments = playerEnchantmentDao.findAllByPrisonPlayer(prisonPlayer);
        if (playerEnchantments.isEmpty()) {
            sender.sendMessage("§cPlayer §6" + prisonPlayer.getPlayerName() + " §chas no enchantments.");
            return;
        }

        sender.sendMessage("§7Enchantments of §6" + prisonPlayer.getPlayerName() + " §7:");
        for (PlayerEnchantment playerEnchantment : playerEnchantments) {
            PickaxeUtil.PickaxeEnchantment pickaxeEnchantment = pickaxeUtil.getPickaxeEnchantment(playerEnchantment.getEnchantmentName());
            StringBuilder enchantmentBuilder = new StringBuilder();
            enchantmentBuilder.append("§7- §6").append(playerEnchantment.getEnchantmentName()).append(" §8[§6").append(playerEnchantment.getEnchantmentLevel());
            if (pickaxeEnchantment == null) {
                enchantmentBuilder.append("§8] §c(Not defined)");
            } else {
                enchantmentBuilder.append("§7/").append(playerEnchantment.getEnchantmentLevel() > pickaxeEnchantment.getMaxLevel() ? "§c" : "§6")
                        .append(pickaxeEnchantment.getMaxLevel()).append("§8]");
            }
            sender.sendMessage(enchantmentBuilder.toString());
        }
    }
}
