package de.hits.prison.server.util;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.server.fileUtil.SettingsUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Component
public class MessageUtil {

    @Autowired
    private static SettingsUtil settingsUtil;

    public static void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, true);
    }

    public static void sendMessage(CommandSender sender, String message, boolean prefix) {
        sender.sendMessage((prefix ? settingsUtil.getPrefix() : "") + message);
    }

    public static void sendFormattedMessage(CommandSender sender, String message) {
        sendFormattedMessage(sender, message, true);
    }

    public static void sendFormattedMessage(CommandSender sender, String message, boolean prefix) {
        sender.sendMessage((prefix ? settingsUtil.getPrefix() : "") + message);
    }

    public static void sendActionbar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

}
