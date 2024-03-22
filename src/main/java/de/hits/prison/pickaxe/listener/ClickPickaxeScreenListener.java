package de.hits.prison.pickaxe.listener;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.screen.ScreenManager;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.pickaxe.screen.PickaxeScreen;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Component
public class ClickPickaxeScreenListener implements Listener {

    @Autowired
    private static PickaxeHelper pickaxeHelper;
    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static ScreenManager screenManager;

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null) {
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (!pickaxeHelper.isCustomPickaxe(itemStack, prisonPlayer)) {
            return;
        }
        if (!player.isSneaking() || (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        screenManager.openScreen(player, new PickaxeScreen(player));
    }
}
