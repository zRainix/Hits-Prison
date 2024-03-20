package de.hits.prison.pickaxe.helper;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerCurrencyDao;
import de.hits.prison.base.model.entity.PlayerCurrency;
import de.hits.prison.base.model.helper.PrisonRepository;
import de.hits.prison.pickaxe.blocks.BlockValue;
import de.hits.prison.pickaxe.enchantment.impl.CubeEnchantment;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class PlayerDrops {

    private static final Random random = new Random();
    private static final List<LastPlayerDrop> lastPlayerDrops = new ArrayList<>();

    @Autowired
    private static PlayerCurrencyDao playerCurrencyDao;

    long volcanicAsh, obsidianShards, exp;

    public PlayerDrops() {
        this(0, 0, 0);
    }

    public PlayerDrops(long volcanicAsh, long obsidianShards, long exp) {
        this.volcanicAsh = volcanicAsh;
        this.obsidianShards = obsidianShards;
        this.exp = exp;
    }

    public long getVolcanicAsh() {
        return volcanicAsh;
    }

    public void setVolcanicAsh(long volcanicAsh) {
        this.volcanicAsh = volcanicAsh;
    }

    public long getObsidianShards() {
        return obsidianShards;
    }

    public void setObsidianShards(long obsidianShards) {
        this.obsidianShards = obsidianShards;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public void add(PlayerDrops playerDrops) {
        this.volcanicAsh += playerDrops.volcanicAsh;
        this.obsidianShards += playerDrops.obsidianShards;
        this.exp += playerDrops.exp;
    }

    public void addAll(List<PlayerDrops> playerDrops) {
        playerDrops.forEach(this::add);
    }

    public PlayerDrops multiply(double multiplier) {
        this.volcanicAsh = (long) ((double) this.volcanicAsh * multiplier);
        this.obsidianShards = (long) ((double) this.obsidianShards * multiplier);
        this.exp = (long) ((double) this.exp * multiplier);
        return this;
    }

    public PlayerDrops multiply(double multiplierAsh, double multiplierShards, double multiplierExp) {
        this.volcanicAsh = (long) ((double) this.volcanicAsh * multiplierAsh);
        this.obsidianShards = (long) ((double) this.obsidianShards * multiplierShards);
        this.exp = (long) ((double) this.exp * multiplierExp);
        return this;
    }

    private static long randomNumber(long min, long max) {
        return random.nextLong((max - min) + 1) + min;
    }

    public PlayerDrops clonePlayerDrops() {
        return new PlayerDrops(this.volcanicAsh, this.obsidianShards, this.exp);
    }

    public static PlayerDrops generate(BlockValue blockValue) {
        if(blockValue == null) {
            return new PlayerDrops();
        }
        return new PlayerDrops(randomNumber(blockValue.getVolcanicAsh() / 2, blockValue.getVolcanicAsh()), randomNumber(blockValue.getObsidianShards() / 2, blockValue.getObsidianShards()), randomNumber(blockValue.getExp() / 2, blockValue.getExp()));
    }

    public static PlayerDrops generateRestricted(BlockValue blockValue, CubeEnchantment.DropFocus dropFocus) {
        return switch(dropFocus) {
            case EXP -> new PlayerDrops(0, 0, randomNumber(blockValue.getExp() / 2, blockValue.getExp()));
            case ASH -> new PlayerDrops(randomNumber(blockValue.getVolcanicAsh() / 2, blockValue.getVolcanicAsh()), 0, 0);
            case SHARDS -> new PlayerDrops(0, randomNumber(blockValue.getObsidianShards() / 2, blockValue.getObsidianShards()), 0);
        };
    }

    public void grantPlayer(OfflinePlayer offlinePlayer) {
        PlayerCurrency playerCurrency = playerCurrencyDao.findByPlayer(offlinePlayer);

        if (playerCurrency == null)
            return;

        boolean found = false;

        if (this.volcanicAsh != 0) {
            playerCurrency.setVolcanicAsh(playerCurrency.getVolcanicAsh().add(BigInteger.valueOf(this.volcanicAsh)).min(PrisonRepository.maxBigIntegerValue));
            found = true;
        }
        if (this.obsidianShards != 0) {
            playerCurrency.setObsidianShards(playerCurrency.getObsidianShards().add(BigInteger.valueOf(this.obsidianShards)).min(PrisonRepository.maxBigIntegerValue));
            found = true;
        }
        if (this.exp != 0) {
            playerCurrency.setExp(playerCurrency.getExp().add(BigInteger.valueOf(this.exp)).min(PrisonRepository.maxBigIntegerValue));
            found = true;
        }

        if (!found)
            return;

        playerCurrencyDao.save(playerCurrency);

        Player player = offlinePlayer.getPlayer();

        if (player == null)
            return;

        LastPlayerDrop lastPlayerDrop = getLastPlayerDrops(player);

        long current = System.currentTimeMillis();

        List<String> drops;

        if (lastPlayerDrop == null) {
            drops = getExtraDropsStrings(null);
            lastPlayerDrop = new LastPlayerDrop(current, player, this);
            lastPlayerDrops.add(lastPlayerDrop);
        } else {
            if ((lastPlayerDrop.lastDropMillis + 1000L) < current) {
                drops = getExtraDropsStrings(null);
                lastPlayerDrop.playerDrops = new PlayerDrops();
            } else {
                drops = getExtraDropsStrings(lastPlayerDrop.playerDrops);
            }
            lastPlayerDrop.lastDropMillis = current;
            lastPlayerDrop.playerDrops.add(this);
        }

        MessageUtil.sendActionbar(player, "§a" + String.join(", ", drops));
    }

    private List<String> getExtraDropsStrings(PlayerDrops extraDrops) {
        List<String> drops = new ArrayList<>();
        if (extraDrops == null) {
            if (this.volcanicAsh != 0) {
                drops.add("§6" + volcanicAsh + " §7Volcanic Ash");
            }
            if (this.obsidianShards != 0) {
                drops.add("§6" + obsidianShards + " §7Obsidian Shards");
            }
            if (this.exp != 0) {
                drops.add("§6" + exp + " §7Exp");
            }
        } else {
            if (this.volcanicAsh != 0 && extraDrops.volcanicAsh != 0) {
                drops.add("§6" + extraDrops.volcanicAsh + " + " + volcanicAsh + " §7Volcanic Ash");
            }
            if (this.obsidianShards != 0 && extraDrops.obsidianShards != 0) {
                drops.add("§6" + extraDrops.obsidianShards + " + " + obsidianShards + " §7Obsidian Shards");
            }
            if (this.exp != 0 && extraDrops.exp != 0) {
                drops.add("§6" + extraDrops.exp + " + " + exp + " §7Exp");
            }
        }
        return drops;
    }

    private LastPlayerDrop getLastPlayerDrops(Player player) {
        for (LastPlayerDrop lastPlayerDrop : lastPlayerDrops) {
            if (lastPlayerDrop.player == player)
                return lastPlayerDrop;
        }
        return null;
    }

    private static class LastPlayerDrop {

        long lastDropMillis;
        Player player;
        PlayerDrops playerDrops;

        public LastPlayerDrop(long lastDropMillis, Player player, PlayerDrops playerDrops) {
            this.lastDropMillis = lastDropMillis;
            this.player = player;
            this.playerDrops = playerDrops;
        }
    }
}

