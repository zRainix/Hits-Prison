package de.hits.prison.prisonPlayer.listener;

import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerCurrencyDao;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PlayerCurrency;
import de.hits.prison.base.model.entity.PrisonPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class PrisonPlayerListener implements Listener {

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static PlayerCurrencyDao playerCurrencyDao;

    @Autowired
    private static PickaxeHelper pickaxeHelper;

    private static final long millisToMinutes = 1000L * 60L;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(p);

        if (prisonPlayer == null) {
            prisonPlayer = new PrisonPlayer();
            prisonPlayer.setPlayerUuid(p.getUniqueId().toString());
            prisonPlayer.setPlaytimeInMinutes(0L);
            pickaxeHelper.buildPlayerPickaxe(p);

            prisonPlayer = prisonPlayerDao.save(prisonPlayer);
        }

        prisonPlayer.setPlayerName(p.getName());
        prisonPlayer.setLastLogin(LocalDateTime.now());

        prisonPlayerDao.save(prisonPlayer);

        PlayerCurrency playerCurrency = playerCurrencyDao.findByPlayer(p);

        if (playerCurrency == null) {
            playerCurrency = new PlayerCurrency();
            playerCurrency.setVolcanicAsh(new BigInteger("0"));
            playerCurrency.setObsidianShards(new BigInteger("0"));
            playerCurrency.setExp(new BigInteger("0"));
            playerCurrency.setRefPrisonPlayer(prisonPlayer);

            playerCurrencyDao.save(playerCurrency);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(p);

        if (prisonPlayer == null) {
            return;
        }

        prisonPlayer.setLastLogout(LocalDateTime.now());

        long additionalPlaytime = calculatePlaytime(prisonPlayer.getLastLogin(), prisonPlayer.getLastLogout()) / millisToMinutes;

        long playtimeInMinutes = prisonPlayer.getPlaytimeInMinutes();

        playtimeInMinutes += additionalPlaytime;

        prisonPlayer.setPlaytimeInMinutes(playtimeInMinutes);

        prisonPlayerDao.save(prisonPlayer);
    }

    private long calculatePlaytime(LocalDateTime start, LocalDateTime end) {
        return Timestamp.valueOf(end).getTime() - Timestamp.valueOf(start).getTime();
    }

}
