package de.hits.prison.pickaxe.screen;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.screen.helper.ClickAction;
import de.hits.prison.base.screen.helper.Screen;
import de.hits.prison.base.screen.helper.ScreenColumn;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.server.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PickaxeUpdateScreen extends Screen {

    @Autowired
    private static PickaxeUtil pickaxeUtil;
    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;

    private final Player player;
    private final Map<String, ScreenColumn> typeColumns;
    private final List<PickaxeUtil.PickaxeEnchantmentType> enchantmentTypes;

    public PickaxeUpdateScreen(Player player) {
        super(getTitle(player), 6);
        this.player = player;
        this.typeColumns = new HashMap<>();
        enchantmentTypes = pickaxeUtil.getPickaxeEnchantmentTypes();
        int[] slots = getStartSlots(enchantmentTypes.size());
        for (int i = 0; i < enchantmentTypes.size(); i++) {
            PickaxeUtil.PickaxeEnchantmentType type = enchantmentTypes.get(i);
            typeColumns.put(type.getName(), new ScreenColumn(this, slots[i], 1));
        }
        typeColumns.values().forEach(this::addScreenColumn);
    }

    public int[] getStartSlots(int columns) {
        return switch (columns) {
            case 1 -> new int[]{4};
            case 2 -> new int[]{3, 5};
            case 3 -> new int[]{2, 4, 6};
            case 4 -> new int[]{1, 3, 5, 7};
            case 5 -> new int[]{2, 3, 4, 5, 6};
            case 6 -> new int[]{1, 2, 3, 5, 6, 7};
            case 7 -> new int[]{1, 2, 3, 4, 5, 6, 7};
            case 8 -> new int[]{0, 1, 2, 3, 5, 6, 7, 8};
            case 9 -> new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
            default -> throw new IllegalStateException("Unexpected value: " + columns);
        };
    }

    @Override
    public void init() {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);
        if (prisonPlayer == null)
            return;

        for (PlayerEnchantment playerEnchantment : prisonPlayer.getPlayerEnchantments()) {
            PickaxeUtil.PickaxeEnchantment enchantment = pickaxeUtil.getPickaxeEnchantment(playerEnchantment.getEnchantmentName());
            if (enchantment == null)
                continue;
            ScreenColumn screenColumn = typeColumns.get(enchantment.getType().getName());
            screenColumn.addItem(buildEnchantmentItem(playerEnchantment, enchantment), new ClickAction() {
                @Override
                public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                    player.sendMessage("Öffne " + playerEnchantment.getEnchantmentName());
                }
            });
        }
    }

    private ItemStack buildEnchantmentItem(PlayerEnchantment playerEnchantment, PickaxeUtil.PickaxeEnchantment enchantment) {
        ItemBuilder itemBuilder = new ItemBuilder(enchantment.getPreviewMaterial()).setAllItemFlags()
                .setDisplayName(enchantment.getRarity().getColorPrefix() + enchantment.getName())
                .addLoreBreak()
                .addLore("§8-- Description --")
                .addLore("§7" + enchantment.getDescription())
                .addLoreBreak();
        /*
        TODO: Add levels
        - if first level: different text: purchase instead of upgrade
        - if first level has activation chance of 1 do not display activation chance
         */
        return itemBuilder.build();
    }

    private static String getTitle(Player player) {
        return "§aPickaxe §6§l" + player.getName();
    }

    public Player getPlayer() {
        return player;
    }
}
