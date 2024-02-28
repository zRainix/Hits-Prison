package de.hits.prison.server.placeholder;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PlayerCurrency;
import de.hits.prison.base.model.entity.PlayerMine;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.mine.helper.MineHelper;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.server.fileUtil.SettingsUtil;
import de.hits.prison.server.scheduler.TpsScheduler;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class PlaceholderHelper {

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static SettingsUtil settingsUtil;
    @Autowired
    private static MineHelper mineHelper;

    List<Placeholder<?>> placeholders = new ArrayList<>();

    public PlaceholderHelper() {
        placeholders.addAll(List.of(
                new Placeholder<>(PlaceholderType.SERVER, "tps", value -> new DecimalFormat("#.00").format(TpsScheduler.getTps())),
                new Placeholder<>(PlaceholderType.SERVER, "primaryColor", SettingsUtil::getPrimaryColor),
                new Placeholder<>(PlaceholderType.SERVER, "pc", SettingsUtil::getPrimaryColor),
                new Placeholder<>(PlaceholderType.SERVER, "secondaryColor", SettingsUtil::getSecondaryColor),
                new Placeholder<>(PlaceholderType.SERVER, "sc", SettingsUtil::getSecondaryColor),
                new Placeholder<>(PlaceholderType.PRISON_PLAYER, "name", PrisonPlayer::getPlayerName),
                new Placeholder<>(PlaceholderType.PRISON_PLAYER, "uuid", PrisonPlayer::getPlayerUuid),
                new Placeholder<>(PlaceholderType.PRISON_PLAYER, "playtimeInMinutes", PrisonPlayer::getPlaytimeInMinutes),
                new Placeholder<>(PlaceholderType.PLAYER_CURRENCY, "volcanicAsh", PlayerCurrency::formatVolcanicAsh),
                new Placeholder<>(PlaceholderType.PLAYER_CURRENCY, "obsidianShards", PlayerCurrency::formatObsidianShards),
                new Placeholder<>(PlaceholderType.PLAYER_CURRENCY, "exp", PlayerCurrency::formatExp),
                new Placeholder<>(PlaceholderType.PLAYER_CURRENCY, "volcanicAshLong", PlayerCurrency::getVolcanicAsh),
                new Placeholder<>(PlaceholderType.PLAYER_CURRENCY, "obsidianShardsLong", PlayerCurrency::getObsidianShards),
                new Placeholder<>(PlaceholderType.PLAYER_CURRENCY, "expLong", PlayerCurrency::getExp),
                new Placeholder<>(PlaceholderType.PLAYER_MINE, "areaLevel", PlayerMine::getAreaLevel),
                new Placeholder<>(PlaceholderType.PLAYER_MINE, "blockLevel", PlayerMine::getBlockLevel),
                new Placeholder<>(PlaceholderType.PLAYER_MINE, "sellLevel", PlayerMine::getSellLevel),
                new Placeholder<>(PlaceholderType.PLAYER_MINE, "rebirthLevel", PlayerMine::getRebirthLevel),
                new Placeholder<>(PlaceholderType.PLAYER_MINE, "templateName", PlayerMine::getTemplateName),
                new Placeholder<>(PlaceholderType.CURRENT_MINE, "ownerName", mine -> mine.getRefPrisonPlayer().getPlayerName()),
                new Placeholder<>(PlaceholderType.CURRENT_MINE, "ownerUuid", mine -> mine.getRefPrisonPlayer().getPlayerUuid()),
                new Placeholder<>(PlaceholderType.CURRENT_MINE, "areaLevel", PlayerMine::getAreaLevel),
                new Placeholder<>(PlaceholderType.CURRENT_MINE, "blockLevel", PlayerMine::getBlockLevel),
                new Placeholder<>(PlaceholderType.CURRENT_MINE, "sellLevel", PlayerMine::getSellLevel),
                new Placeholder<>(PlaceholderType.CURRENT_MINE, "rebirthLevel", PlayerMine::getRebirthLevel),
                new Placeholder<>(PlaceholderType.CURRENT_MINE, "templateName", PlayerMine::getTemplateName)
        ));
    }

    public StrSubstitutor getPlaceholderSubstitutor(OfflinePlayer player, String string) {
        PlaceholderHelper.PlaceholderParser parser = new PlaceholderHelper.PlayerPlaceholderParser(player);

        Map<String, String> placeholderReplacements = new HashMap<>();
        placeholders.forEach(placeholder -> {
            String name = placeholder.getPlaceholderType().getName() + "." + placeholder.getName();
            if (string == null || string.contains(name))
                placeholderReplacements.put(name, parser.getValue(placeholder));
        });

        return new StrSubstitutor(placeholderReplacements);
    }

    public StrSubstitutor getPlaceholderSubstitutor(OfflinePlayer offlinePlayer) {
        return getPlaceholderSubstitutor(offlinePlayer, null);
    }

    public static abstract class PlaceholderParser {

        public abstract String getValue(Placeholder placeholder);

    }

    public static class PlayerPlaceholderParser extends PlaceholderParser {

        OfflinePlayer player;
        PrisonPlayer prisonPlayer;

        public PlayerPlaceholderParser(OfflinePlayer player) {
            this.player = player;
            this.prisonPlayer = prisonPlayerDao.findByPlayer(player);
            if (this.prisonPlayer == null)
                return;
        }

        public String getValue(Placeholder placeholder) {
            if (prisonPlayer == null)
                return "n/a";

            prisonPlayerDao.update(prisonPlayer, prisonPlayer.getId());

            if (placeholder.getPlaceholderType() == PlaceholderType.SERVER) {
                return placeholder.get(settingsUtil);
            }
            if (placeholder.getPlaceholderType() == PlaceholderType.PRISON_PLAYER) {
                return placeholder.get(prisonPlayer);
            }
            if (placeholder.getPlaceholderType() == PlaceholderType.PLAYER_CURRENCY) {
                return placeholder.get(prisonPlayer.getPlayerCurrency());
            }
            if (placeholder.getPlaceholderType() == PlaceholderType.PLAYER_MINE) {
                return placeholder.get(prisonPlayer.getPlayerMine());
            }
            if (placeholder.getPlaceholderType() == PlaceholderType.CURRENT_MINE) {
                if (!player.isOnline())
                    return "n/a";
                MineWorld mineWorld = mineHelper.getMineWorld(player.getPlayer().getWorld());
                if (mineWorld == null)
                    return "n/a";

                PrisonPlayer mineOwner = mineWorld.getPrisonPlayer();
                prisonPlayerDao.update(mineOwner, mineOwner.getId());

                return placeholder.get(mineOwner.getPlayerMine());
            }
            return null;
        }
    }

    public static class ServerPlaceHolderParser extends PlaceholderParser {

        public String getValue(Placeholder placeholder) {
            if (placeholder.getPlaceholderType() == PlaceholderType.SERVER) {
                return placeholder.get(settingsUtil);
            }
            return null;
        }
    }

    public static class Placeholder<T> {

        PlaceholderType<T> placeholderType;
        String name;
        Function<T, Object> function;

        public Placeholder(PlaceholderType<T> placeholderType, String name, Function<T, Object> function) {
            this.placeholderType = placeholderType;
            this.name = name;
            this.function = function;
        }

        public PlaceholderType<T> getPlaceholderType() {
            return placeholderType;
        }

        public String getName() {
            return name;
        }

        public String get(T value) {
            return function.apply(value).toString();
        }
    }

    public static class PlaceholderType<T> {

        public static final PlaceholderType<SettingsUtil> SERVER = new PlaceholderType<>("server");
        public static final PlaceholderType<PrisonPlayer> PRISON_PLAYER = new PlaceholderType<>("prisonPlayer");
        public static final PlaceholderType<PlayerCurrency> PLAYER_CURRENCY = new PlaceholderType<>("playerCurrency");
        public static final PlaceholderType<PlayerMine> PLAYER_MINE = new PlaceholderType<>("playerMine");
        public static final PlaceholderType<PlayerMine> CURRENT_MINE = new PlaceholderType<>("currentMine");

        final String name;

        private PlaceholderType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
