package de.hits.prison.mine.command;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.anno.BaseCommand;
import de.hits.prison.base.command.anno.CommandParameter;
import de.hits.prison.base.command.anno.SubCommand;
import de.hits.prison.base.command.helper.AdvancedCommand;
import de.hits.prison.base.model.dao.MineTrustedPlayerDao;
import de.hits.prison.base.model.dao.PlayerMineDao;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.MineTrustedPlayer;
import de.hits.prison.base.model.entity.PlayerMine;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.screen.ScreenManager;
import de.hits.prison.mine.fileUtil.MineUtil;
import de.hits.prison.mine.helper.MineHelper;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.mine.screen.MineCreatorScreen;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MineCommand extends AdvancedCommand {

    private final Logger logger = Bukkit.getLogger();

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static PlayerMineDao playerMineDao;
    @Autowired
    private static MineTrustedPlayerDao mineTrustedPlayerDao;
    @Autowired
    private static MineHelper mineHelper;
    @Autowired
    private static MineUtil mineUtil;
    @Autowired
    private static ScreenManager screenManager;

    private static final String MODIFY_BLOCK_LEVEL_PERMISSION = "prison.mine.blockLevel.modify";
    private static final String MODIFY_AREA_LEVEL_PERMISSION = "prison.mine.areaLevel.modify";
    private static final String RESET_CACHE_PERMISSION = "prison.mine.resetCache";


    public MineCommand() {
        super("mine");
    }

    @BaseCommand
    public void mine(Player player) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);
        if (prisonPlayer == null)
            return;
        prisonPlayerDao.save(prisonPlayer);
        PlayerMine playerMine = prisonPlayer.getPlayerMine();

        if (playerMine == null) {
            screenManager.openScreen(player, new MineCreatorScreen());
            return;
        }

        MineHelper.RegisteredMineTemplate template = mineHelper.getRegisteredMineTemplate(playerMine.getTemplateName());

        if (template == null) {
            MessageUtil.sendMessage(player, "§cYour template is not registered on this server. Please contact staff.");
            logger.warning("Mine template " + playerMine.getTemplateName() + " not found for player " + player.getName() + ".");
            return;
        }

        MessageUtil.sendMessage(player, "§7Teleporting to mine...");

        try {
            MineWorld mineWorld = mineHelper.generateMineWorld(prisonPlayer, template);
            mineHelper.teleportPlayerToMine(player, mineWorld);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "§cAn error occurred.");
            logger.log(Level.SEVERE, "Error while loading mine world for player " + player.getName(), e);
        }
    }

    @SubCommand(subCommand = "reset")
    public void resetMine(Player player) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        MineWorld mineWorld = mineHelper.getMineWorld(player.getWorld());

        if (mineWorld == null) {
            MessageUtil.sendMessage(player, "§cYou are not in a mine.");
            return;
        }

        if (!Objects.equals(mineWorld.getPrisonPlayer().getId(), prisonPlayer.getId())) {
            MineTrustedPlayer trustedPlayer = mineTrustedPlayerDao.findByPrisonPlayerAndPlayerMineOwner(prisonPlayer, mineWorld.getPrisonPlayer());
            if (trustedPlayer == null) {
                MessageUtil.sendMessage(player, "§cYou are not a trusted player.");
                return;
            }
        }

        mineWorld.updateMine();
        MessageUtil.sendMessage(player, "§7Mine was reset.");
    }

    @SubCommand(subCommand = "public")
    public void publicMine(Player player) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        PlayerMine playerMine = playerMineDao.findByPrisonPlayer(prisonPlayer);

        if (playerMine == null) {
            MessageUtil.sendMessage(player, "§cYou have not loaded your mine yet.");
            return;
        }

        if (!playerMine.isPrivateMine()) {
            MessageUtil.sendMessage(player, "§7Your mine is already public.");
            return;
        }

        playerMine.setPrivateMine(false);

        playerMineDao.save(playerMine);

        MessageUtil.sendMessage(player, "§7Your mine is now §apublic§7.");
    }

    @SubCommand(subCommand = "private")
    public void privateMine(Player player) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        PlayerMine playerMine = playerMineDao.findByPrisonPlayer(prisonPlayer);

        if (playerMine == null) {
            MessageUtil.sendMessage(player, "§cYou have not loaded your mine yet.");
            return;
        }

        if (playerMine.isPrivateMine()) {
            MessageUtil.sendMessage(player, "§7Your mine is already private.");
            return;
        }

        playerMine.setPrivateMine(true);

        playerMineDao.save(playerMine);

        MessageUtil.sendMessage(player, "§7Your mine is now §cprivate§7.");
    }

    @SubCommand(subCommand = "trust")
    public void trustPlayer(Player player, @CommandParameter(name = "player") PrisonPlayer trusted) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        if (Objects.equals(trusted.getId(), prisonPlayer.getId())) {
            MessageUtil.sendMessage(player, "§cYou cannot trust yourself.");
            return;
        }

        PlayerMine playerMine = prisonPlayer.getPlayerMine();

        if (playerMine == null) {
            MessageUtil.sendMessage(player, "§cYou have not loaded your mine yet.");
            return;
        }

        MineTrustedPlayer trustedPlayer = mineTrustedPlayerDao.findByPrisonPlayerAndPlayerMine(trusted, playerMine);
        if (trustedPlayer != null) {
            MessageUtil.sendMessage(player, "§7This player is already trusted.");
            return;
        }

        trustedPlayer = new MineTrustedPlayer();
        trustedPlayer.setRefPlayerMine(playerMine);
        trustedPlayer.setRefTrustedPrisonPlayer(trusted);

        mineTrustedPlayerDao.save(trustedPlayer);

        MessageUtil.sendMessage(player, "§7This player is now §atrusted§7.");
    }

    @SubCommand(subCommand = "untrust")
    public void untrustPlayer(Player player, @CommandParameter(name = "player") PrisonPlayer untrusted) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        if (Objects.equals(untrusted.getId(), prisonPlayer.getId())) {
            MessageUtil.sendMessage(player, "§cYou cannot untrust yourself.");
            return;
        }

        PlayerMine playerMine = prisonPlayer.getPlayerMine();

        if (playerMine == null) {
            MessageUtil.sendMessage(player, "§cYou have not loaded your mine yet.");
            return;
        }

        MineTrustedPlayer trustedPlayer = mineTrustedPlayerDao.findByPrisonPlayerAndPlayerMine(untrusted, playerMine);
        if (trustedPlayer == null) {
            MessageUtil.sendMessage(player, "§7This player is not trusted.");
            return;
        }

        mineTrustedPlayerDao.delete(trustedPlayer);

        MessageUtil.sendMessage(player, "§7This player is now §cnot trusted§7.");
    }

    @SubCommand(subCommand = "trusted")
    public void trustedPlayers(Player player) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        PlayerMine playerMine = prisonPlayer.getPlayerMine();

        if (playerMine == null) {
            MessageUtil.sendMessage(player, "§cYou have not loaded your mine yet.");
            return;
        }

        List<MineTrustedPlayer> trustedPlayers = playerMine.getTrustedPlayers();

        if (trustedPlayers.isEmpty()) {
            MessageUtil.sendMessage(player, "§cYou have no trusted players.");
            return;
        }

        MessageUtil.sendMessage(player, "§7Your currently trusted players:");
        for (MineTrustedPlayer trustedPlayer : trustedPlayers) {
            MessageUtil.sendMessage(player, "§8- §b" + trustedPlayer.getRefTrustedPrisonPlayer().getPlayerName());
        }
    }

    @SubCommand(subCommand = "visit")
    public void visitMine(Player player, @CommandParameter(name = "player") PrisonPlayer visit) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        if (Objects.equals(visit.getId(), prisonPlayer.getId())) {
            MessageUtil.sendMessage(player, "§cUse §6/mine §cto go to your mine.");
            return;
        }

        PlayerMine playerMine = visit.getPlayerMine();

        if (playerMine == null) {
            MessageUtil.sendMessage(player, "§cThis player has no mine loaded yet.");
            return;
        }

        if (playerMine.isPrivateMine()) {
            MineTrustedPlayer trustedPlayer = mineTrustedPlayerDao.findByPrisonPlayerAndPlayerMine(prisonPlayer, playerMine);
            if (trustedPlayer == null) {
                MessageUtil.sendMessage(player, "§7This island is private.");
                return;
            }
        }

        MineHelper.RegisteredMineTemplate template = mineHelper.getRegisteredMineTemplate(playerMine.getTemplateName());

        if (template == null) {
            MessageUtil.sendMessage(player, "§cThis players template is not registered on this server.");
            logger.warning("Mine template " + playerMine.getTemplateName() + " not found for player " + visit.getPlayerName() + ".");
            return;
        }

        MessageUtil.sendMessage(player, "§7Visiting mine of §b" + visit.getPlayerName() + "§7...");

        try {
            MineWorld mineWorld = mineHelper.generateMineWorld(visit, template);
            mineHelper.teleportPlayerToMine(player, mineWorld);
        } catch (Exception e) {
            MessageUtil.sendMessage(player, "§cAn error occurred.");
            logger.log(Level.SEVERE, "Error while loading mine world for player " + player.getName(), e);
        }
    }
    @SubCommand(subCommand = "setBlockLevel", permission = MODIFY_BLOCK_LEVEL_PERMISSION)
    public void setBlockLevel(CommandSender sender, @CommandParameter(name = "player") PrisonPlayer prisonPlayer, @CommandParameter(name = "blockLevel") int blockLevel) {
        PlayerMine playerMine = prisonPlayer.getPlayerMine();

        if (playerMine == null) {
            MessageUtil.sendMessage(sender, "§cPlayer has not mine.");
            return;
        }

        int previousBlockLevel = playerMine.getBlockLevel();

        playerMine.setBlockLevel(blockLevel);
        playerMineDao.save(playerMine);

        MessageUtil.sendMessage(sender, "§7Block level of player §b" + prisonPlayer.getPlayerName() + " §7was changed from §b" + previousBlockLevel + " §7to §b" + blockLevel + "§7.");

        MineWorld mineWorld = mineHelper.getMineWorld(prisonPlayer);

        if (mineWorld == null)
            return;

        mineWorld.updateMine();

        MessageUtil.sendMessage(sender, "§7Player mine was updated.");
    }

    @SubCommand(subCommand = "getBlockLevel", permission = RESET_CACHE_PERMISSION)
    public void getBlockLevel(CommandSender sender, @CommandParameter(name = "player") PrisonPlayer prisonPlayer) {
        PlayerMine playerMine = prisonPlayer.getPlayerMine();

        if (playerMine == null) {
            MessageUtil.sendMessage(sender, "§cPlayer has not mine.");
            return;
        }

        MessageUtil.sendMessage(sender, "§7Block level of player §b" + prisonPlayer.getPlayerName() + " §7is §b" + playerMine.getBlockLevel() + "§7.");
    }

    @SubCommand(subCommand = "setAreaLevel", permission = MODIFY_AREA_LEVEL_PERMISSION)
    public void setAreaLevel(CommandSender sender, @CommandParameter(name = "player") PrisonPlayer prisonPlayer, @CommandParameter(name = "areaLevel") int areaLevel) {
        PlayerMine playerMine = prisonPlayer.getPlayerMine();

        if (playerMine == null) {
            MessageUtil.sendMessage(sender, "§cPlayer has not mine.");
            return;
        }

        int previousAreaLevel = playerMine.getAreaLevel();

        playerMine.setAreaLevel(areaLevel);
        playerMineDao.save(playerMine);

        MessageUtil.sendMessage(sender, "§7Area level of player §b" + prisonPlayer.getPlayerName() + " §7was changed from §b" + previousAreaLevel + " §7to §b" + areaLevel + "§7.");

        MineWorld mineWorld = mineHelper.getMineWorld(prisonPlayer);

        if (mineWorld == null)
            return;

        mineWorld.updateMine();

        MessageUtil.sendMessage(sender, "§7Player mine was updated.");
    }

    @SubCommand(subCommand = "getAreaLevel", permission = MODIFY_AREA_LEVEL_PERMISSION)
    public void getAreaLevel(CommandSender sender, @CommandParameter(name = "player") PrisonPlayer prisonPlayer) {
        PlayerMine playerMine = prisonPlayer.getPlayerMine();

        if (playerMine == null) {
            MessageUtil.sendMessage(sender, "§cPlayer has not mine.");
            return;
        }

        MessageUtil.sendMessage(sender, "§7Area level of player §b" + prisonPlayer.getPlayerName() + " §7is §b" + playerMine.getAreaLevel() + "§7.");
    }

    @SubCommand(subCommand = "resetCache", permission = RESET_CACHE_PERMISSION)
    public void resetCache(CommandSender sender) {
        mineUtil.getBlockLevelCache().clear();
        mineUtil.getAreaLevelCache().clear();
        MessageUtil.sendMessage(sender, "§7Block and area level cache was cleared.");
    }

}
