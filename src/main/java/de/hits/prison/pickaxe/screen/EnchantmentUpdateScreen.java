package de.hits.prison.pickaxe.screen;

import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.screen.helper.Screen;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.screen.helper.PickaxeScreensHelper;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnchantmentUpdateScreen extends Screen {

    private final PickaxeUtil.PickaxeEnchantment enchantment;
    private final PlayerEnchantment playerEnchantment;

    public EnchantmentUpdateScreen(PickaxeUtil.PickaxeEnchantment enchantment, PlayerEnchantment playerEnchantment, Screen parent) {
        super(enchantment.getFullName(), 3, parent);
        this.enchantment = enchantment;
        this.playerEnchantment = playerEnchantment;
    }

    @Override
    protected void init() {
        List<Integer> nextLevels = PickaxeScreensHelper.getNextLevels(enchantment, playerEnchantment);
        ItemStack preview = PickaxeScreensHelper.buildEnchantmentItem(enchantment, playerEnchantment);
        if (nextLevels == null || nextLevels.isEmpty()) {
            setItem(4 + 9, preview, null);
            return;
        }
        setItem(4, preview, null);
        int[] slots = getCenteredSlot(nextLevels.size());
        for (int i = 0; i < nextLevels.size(); i++) {
            int slot = slots[i] + 9;
            // TODO: add buy screen
            setItem(slot, PickaxeScreensHelper.buildLevelItem(enchantment, playerEnchantment, nextLevels.get(i)), null);
        }
    }
}
