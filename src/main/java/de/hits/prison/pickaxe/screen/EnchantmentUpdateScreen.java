package de.hits.prison.pickaxe.screen;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerCellsGivingDao;
import de.hits.prison.base.model.dao.PlayerCurrencyDao;
import de.hits.prison.base.model.dao.PlayerEnchantmentDao;
import de.hits.prison.base.model.entity.PlayerCurrency;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.screen.helper.ClickAction;
import de.hits.prison.base.screen.helper.Screen;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.pickaxe.screen.helper.PickaxeScreensHelper;
import de.hits.prison.server.screen.ConfirmationScreen;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.List;

@Component
public class EnchantmentUpdateScreen extends Screen {

    private final PickaxeUtil.PickaxeEnchantment enchantment;
    private final PlayerEnchantment playerEnchantment;


    @Autowired
    private static PickaxeHelper pickaxeHelper;
    @Autowired
    private static PlayerCurrencyDao playerCurrencyDao;
    @Autowired
    private static PlayerEnchantmentDao playerEnchantmentDao;

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
            int level = nextLevels.get(i);
            int playerLevel = playerEnchantment != null ? playerEnchantment.getEnchantmentLevel() : 0;
            setItem(slot, PickaxeScreensHelper.buildLevelItem(enchantment, playerEnchantment, level), new ClickAction() {
                @Override
                public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                    screenManager.openScreen(player, new ConfirmationScreen(preview, new ClickAction() {
                        @Override
                        public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                            BigInteger price = PickaxeScreensHelper.calculatePrice(enchantment, playerLevel, level);
                            PlayerCurrency playerCurrency = playerCurrencyDao.findByPlayer(player);

                            if(playerCurrency == null || price.compareTo(playerCurrency.getObsidianShards()) > 0) {
                                screenManager.openScreen(player, null);
                                MessageUtil.sendMessage(player, "§cInsufficient funds!");
                                return;
                            }

                            playerCurrency.setObsidianShards(playerCurrency.getObsidianShards().subtract(price));
                            playerCurrencyDao.save(playerCurrency);

                            PlayerEnchantment updatedEnchantment = playerEnchantment;
                            if(updatedEnchantment == null) {
                                updatedEnchantment = new PlayerEnchantment();
                                updatedEnchantment.setRefPrisonPlayer(playerCurrency.getRefPrisonPlayer());
                                updatedEnchantment.setEnchantmentName(enchantment.getName());
                            }
                            updatedEnchantment.setEnchantmentLevel(level);
                            playerEnchantmentDao.save(updatedEnchantment);
                            pickaxeHelper.checkPlayerInventory(player);

                            if(level == 1) {
                                MessageUtil.sendMessage(player, "§7Activated Enchantment " + enchantment.getFullName());
                            } else {
                                MessageUtil.sendMessage(player, "§7Advanced Enchantment " + enchantment.getRarity().getColorPrefix() + enchantment.getName() + " §7to level §b" + level);
                            }
                            screenManager.openScreen(player, null);
                        }
                    }, screen));
                }
            });
        }
    }
}
