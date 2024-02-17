package de.hits.prison.pickaxe.screen;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerEnchantmentDao;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.screen.helper.ClickAction;
import de.hits.prison.base.screen.helper.Screen;
import de.hits.prison.base.screen.helper.ScreenColumn;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.screen.helper.PickaxeScreensHelper;
import de.hits.prison.server.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PickaxeScreen extends Screen {

    @Autowired
    private static PickaxeUtil pickaxeUtil;
    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static PlayerEnchantmentDao playerEnchantmentDao;

    private final Player player;
    private final Map<String, ScreenColumn> typeColumns;

    public PickaxeScreen(Player player) {
        this(player, null);
    }

    public PickaxeScreen(Player player, Screen parent) {
        super(getTitle(player), 6, parent);
        this.player = player;
        this.typeColumns = new HashMap<>();
        List<PickaxeUtil.PickaxeEnchantmentType> enchantmentTypes = pickaxeUtil.getPickaxeEnchantmentTypes();
        int[] slots = getCenteredSlot(enchantmentTypes.size());
        for (int i = 0; i < enchantmentTypes.size(); i++) {
            PickaxeUtil.PickaxeEnchantmentType type = enchantmentTypes.get(i);
            typeColumns.put(type.getName(), new ScreenColumn(this, slots[i], 1));
        }
        typeColumns.values().forEach(this::addScreenColumn);
    }

    @Override
    public void init() {
        for (PickaxeUtil.PickaxeEnchantment enchantment : pickaxeUtil.getPickaxeEnchantments().stream().sorted(Comparator.comparingInt(o -> o.getRarity().getOrder())).toList()) {
            PlayerEnchantment playerEnchantment = playerEnchantmentDao.findByPlayerAndEnchantmentName(player, enchantment.getName());
            ScreenColumn screenColumn = typeColumns.get(enchantment.getType().getName());
            screenColumn.addItem(PickaxeScreensHelper.buildEnchantmentItem(enchantment, playerEnchantment), new ClickAction() {
                @Override
                public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                    screenManager.openScreen(player, new EnchantmentUpdateScreen(enchantment, playerEnchantment, screen));
                }
            });
        }
    }

    private static String getTitle(Player player) {
        return "§aPickaxe §6§l" + player.getName();
    }

    public Player getPlayer() {
        return player;
    }
}
