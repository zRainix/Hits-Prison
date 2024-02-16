package de.hits.prison.base.command.parser;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.helper.ArgumentParser;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PrisonPlayer;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PrisonPlayerArgumentParser extends ArgumentParser<PrisonPlayer> {

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;

    public PrisonPlayerArgumentParser() {
        super(PrisonPlayer.class);
    }

    @Override
    public PrisonPlayer parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException {
        PrisonPlayer player = prisonPlayerDao.findByName(arg);
        if (player == null) {
            try {
                UUID uuid = UUID.fromString(arg);
                player = prisonPlayerDao.findByUuid(uuid);
            } catch (Exception e) {
            }
        }
        if (player == null) {
            throw new IllegalArgumentException("§cPrisonPlayer not found: §6" + arg);
        }
        return player;
    }

    @Override
    public String format(PrisonPlayer value) {
        return String.valueOf(value.getPlayerUuid());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        List<String> completions = new ArrayList<>();
        for (PrisonPlayer player : prisonPlayerDao.findAll()) {
            if (player.getPlayerName().toLowerCase().startsWith(arg.toLowerCase())) {
                completions.add(player.getPlayerName());
            }
        }
        return completions;
    }
}