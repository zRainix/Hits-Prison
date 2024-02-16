package de.hits.prison.pickaxe.scheduler;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.scheduler.anno.Scheduler;
import de.hits.prison.base.scheduler.helper.CustomScheduler;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImplManager;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@Component
@Scheduler
public class PlayerPotionEffectScheduler extends CustomScheduler {

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static PickaxeEnchantmentImplManager pickaxeEnchantmentImplManager;
    @Autowired
    private static PickaxeHelper pickaxeHelper;

    private static final long one_second = 20L;

    public PlayerPotionEffectScheduler() {
        super(one_second, one_second);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);
            if (prisonPlayer == null)
                continue;

            if (!pickaxeHelper.isCustomPickaxe(player.getInventory().getItemInMainHand(), prisonPlayer) && !pickaxeHelper.isCustomPickaxe(player.getInventory().getItemInOffHand(), prisonPlayer))
                continue;

            List<PlayerEnchantment> playerEnchantments = prisonPlayer.getPlayerEnchantments();
            playerEnchantments.forEach(playerEnchantment -> pickaxeEnchantmentImplManager.getEnchantmentsImplementations().stream().filter(pickaxeEnchantmentImpl -> pickaxeEnchantmentImpl.getEnchantmentName().equals(playerEnchantment.getEnchantmentName())).forEach(pickaxeEnchantmentImpl -> {
                PotionEffect potionEffect = pickaxeEnchantmentImpl.getVanillaPotionEffect(prisonPlayer, playerEnchantment);
                if (potionEffect != null) {
                    player.addPotionEffect(potionEffect);
                }
            }));
        }
    }
}
