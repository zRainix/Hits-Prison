package de.hits.prison.command.parser;

import de.hits.prison.command.helper.ArgumentParser;
import de.hits.prison.command.anno.PlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class PlayerArgumentParser extends ArgumentParser<Player> {
    public PlayerArgumentParser() {
        super(Player.class);
    }

    @Override
    public Player parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException {
        Player player = Bukkit.getPlayer(arg);
        if (player == null) {
            throw new IllegalArgumentException("§cPlayer not found: §6" + arg);
        }
        if (isFriend(parameter) && areFriends(sender, player)) {
            throw new IllegalArgumentException("§cYou are not friends with this player.");
        }
        return player;
    }

    @Override
    public String format(Player value) {
        return value.getName();
    }

    public boolean isFriend(Parameter parameter) {
        if (parameter.isAnnotationPresent(PlayerArgument.class)) {
            PlayerArgument playerArgument = parameter.getAnnotation(PlayerArgument.class);
            return playerArgument.isFriend();
        }
        return false;
    }

    public boolean areFriends(CommandSender sender, Player player) {
        // TODO
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        List<String> completions = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(arg.toLowerCase()) && (!isFriend(parameter) || areFriends(sender, player))) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}