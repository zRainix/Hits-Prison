package de.hits.prison.command.parser;

import de.hits.prison.command.helper.ArgumentParser;
import de.hits.prison.command.anno.PlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class OfflinePlayerArgumentParser extends ArgumentParser<OfflinePlayer> {
    public OfflinePlayerArgumentParser() {
        super(OfflinePlayer.class);
    }

    @Override
    public OfflinePlayer parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException {
        OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
        if (player == null) {
            throw new IllegalArgumentException("§cOfflinePlayer not found: §6" + arg);
        }
        if (isFriend(parameter) && areFriends(sender, player)) {
            throw new IllegalArgumentException("§cYou are not friends with this player.");
        }
        return player;
    }

    @Override
    public String format(OfflinePlayer value) {
        return value.getName();
    }

    public boolean isFriend(Parameter parameter) {
        if (parameter.isAnnotationPresent(PlayerArgument.class)) {
            PlayerArgument playerArgument = parameter.getAnnotation(PlayerArgument.class);
            return playerArgument.isFriend();
        }
        return false;
    }

    public boolean areFriends(CommandSender sender, OfflinePlayer player) {
        // TODO
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        List<String> completions = new ArrayList<>();
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName().toLowerCase().startsWith(arg.toLowerCase()) && (!isFriend(parameter) || areFriends(sender, player))) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}