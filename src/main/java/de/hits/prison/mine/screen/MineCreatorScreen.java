package de.hits.prison.mine.screen;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerMineDao;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PlayerMine;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.screen.helper.ClickAction;
import de.hits.prison.base.screen.helper.Screen;
import de.hits.prison.mine.fileUtil.MineTemplateUtil;
import de.hits.prison.mine.helper.MineHelper;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.server.util.ItemBuilder;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MineCreatorScreen extends Screen {

    @Autowired
    private static Logger logger;

    @Autowired
    private static MineHelper mineHelper;
    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static PlayerMineDao playerMineDao;

    public MineCreatorScreen() {
        super("§aSelect mine template", calculateRows());
    }

    @Override
    protected void init() {
        for (MineHelper.RegisteredMineTemplate template : mineHelper.getRegisteredTemplates()) {
            addItem(buildItem(template), new ClickAction() {
                @Override
                public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                    if (!event.getClick().isMouseClick())
                        return;
                    PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);
                    if (prisonPlayer == null)
                        return;

                    if (event.isLeftClick()) {
                        player.closeInventory();
                        PlayerMine playerMine = playerMineDao.findByPlayer(player);
                        if (playerMine == null) {
                            playerMine = new PlayerMine();
                            playerMine.setAreaLevel(1);
                            playerMine.setBlockLevel(1);
                            playerMine.setPrivateMine(true);
                            playerMine.setRebirthLevel(1);
                            playerMine.setRefPrisonPlayer(prisonPlayer);
                        }
                        playerMine.setTemplateName(template.getTemplateUtil().getName());

                        playerMineDao.save(playerMine);
                        MessageUtil.sendMessage(player, "§7Teleporting to mine...");
                        try {
                            MineWorld mineWorld = mineHelper.generateMineWorld(prisonPlayer, template);
                            if (mineWorld == null)
                                throw new Exception("Mine world could not be loaded.");
                            mineHelper.teleportPlayerToMine(player, mineWorld);
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error while loading world for player " + player.getName(), e);
                            MessageUtil.sendMessage(player, "§cAn error occurred.");
                            player.closeInventory();
                        }
                    } else if (event.isRightClick()) {
                        player.closeInventory();
                        MessageUtil.sendMessage(player, "§7Teleporting to preview §b" + template.getTemplateUtil().getName() + "§7...");
                        mineHelper.teleportPlayerToMine(player, template.getTemplateWorld(), template.getTemplateUtil());
                    }
                }
            });
        }
    }

    private ItemStack buildItem(MineHelper.RegisteredMineTemplate template) {
        MineTemplateUtil util = template.getTemplateUtil();
        return new ItemBuilder(util.getPreviewMaterial())
                .setDisplayName("§b" + util.getName())
                .addLoreBreak()
                .addLoreHeading("Builder")
                .addLore("§7" + util.getBuilder())
                .addLoreBreak()
                .addLore("§aLeft click to select.")
                .addLore("§aRight click for preview.")
                .build();
    }

    private static int calculateRows() {
        return 3 + ((mineHelper.getRegisteredTemplates().size() - 1) / 9);
    }
}
