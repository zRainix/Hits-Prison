package de.hits.prison.base.command.helper;

import de.hits.prison.base.command.anno.BaseCommand;
import de.hits.prison.base.command.anno.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class AdvancedCommand extends SimpleCommand {

    private final Logger logger = Bukkit.getLogger();

    public AdvancedCommand(String commandName) {
        super(commandName);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(this.commandName)) {
            return true;
        }

        Method method = findBaseCommand(this.getClass());
        if (method == null) {
            executeSubCommands(sender, args);
            return true;
        }

        if (args.length > 0) {
            executeSubCommands(sender, args);
            return true;
        }

        BaseCommand annotation = method.getAnnotation(BaseCommand.class);
        if (annotation != null) {
            if (!hasPermission(sender, annotation.permission()) || !hasOp(sender, annotation.op())) {
                sender.sendMessage("§cYou don't have permission to execute this command.");
                return true;
            }
        }

        Parameter[] parameters = method.getParameters();

        if (parameters.length != 1) {
            sender.sendMessage("§cBase command must only have parameter for CommandSender or Player.");
            return true;
        }

        try {
            method.setAccessible(true);
            Object[] parsedArgs = parseArgs(sender, parameters, args);
            method.invoke(this, parsedArgs);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§c" + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while executing command.", e);
        }

        return true;
    }

    public void executeSubCommands(CommandSender sender, String[] args) {

        List<Method> subCommands = findSubCommands(this.getClass());

        String subCommandHelp = generateSubCommandsHelp(subCommands.stream()
                .filter(method -> method.getParameters()[0].getType() != Player.class || sender instanceof Player)
                .map(method -> method.getAnnotation(SubCommand.class))
                .filter(subCommand -> hasPermission(sender, subCommand.permission()) && hasOp(sender, subCommand.op()))
                .map(SubCommand::subCommand)
                .collect(Collectors.toList()));

        if (args.length == 0) {
            sender.sendMessage("§cPlease use: §6" + subCommandHelp);
            return;
        }

        String subCommandName = args[0];

        for (Method subCommand : subCommands) {
            SubCommand subCommandAnno = subCommand.getAnnotation(SubCommand.class);
            if (!hasPermission(sender, subCommandAnno.permission()) || !hasOp(sender, subCommandAnno.op())) {
                sender.sendMessage("§cYou don't have permission to execute this command.");
                return;
            }
            String name = subCommandAnno.subCommand();
            if (name.equalsIgnoreCase(subCommandName)) {
                executeSubCommand(sender, args, name, subCommand);
                return;
            }
        }

        sender.sendMessage("§cPlease use: §6" + subCommandHelp);
    }


    public void executeSubCommand(CommandSender sender, String[] args, String subCommandName, Method method) {

        Parameter[] parameters = method.getParameters();

        String[] subCommandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subCommandArgs, 0, subCommandArgs.length);

        int[] minMaxLength = getMinMaxLength(parameters);

        int minLength = minMaxLength[0];
        int maxLength = minMaxLength[1];

        int argsLength = subCommandArgs.length;

        if (argsLength < minLength || argsLength > maxLength) {
            sender.sendMessage("§cPlease use: §6" + generateCommandHelp(parameters, subCommandName));
            return;
        }

        try {
            method.setAccessible(true);
            Object[] parsedArgs = parseArgs(sender, parameters, subCommandArgs, subCommandName);
            method.invoke(this, parsedArgs);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§c" + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while executing sub command.", e);
        }
    }

    protected String generateSubCommandsHelp(List<String> subCommands) {
        return "/" + this.commandName + " " +
                "(" + String.join(" | ", subCommands) + ")";
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completion = List.of();

        List<Method> subCommands = findSubCommands(this.getClass());

        if (args.length == 0) {
            return completion;
        }

        String subCommandName = args[0];

        if (args.length == 1) {
            return subCommands.stream().filter(method -> method.getParameters()[0].getType() != Player.class || sender instanceof Player)
                    .map(method -> method.getAnnotation(SubCommand.class))
                    .filter(subCommand -> hasPermission(sender, subCommand.permission()) && hasOp(sender, subCommand.op()))
                    .map(SubCommand::subCommand)
                    .filter(name -> name.toLowerCase().startsWith(subCommandName.toLowerCase())).collect(Collectors.toList());
        }

        String name;
        Method method = null;

        for (Method subCommand : subCommands) {
            SubCommand subCommandAnno = subCommand.getAnnotation(SubCommand.class);

            name = subCommandAnno.subCommand();

            if (name.equalsIgnoreCase(subCommandName)) {
                method = subCommand;
                break;
            }
        }

        if (method == null) {
            return completion;
        }

        String[] subCommandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subCommandArgs, 0, subCommandArgs.length);

        int argLen = subCommandArgs.length;

        method.setAccessible(true);
        Parameter[] parameters = method.getParameters();

        if (!(argLen < parameters.length)) {
            return completion;
        }

        Parameter parameter = parameters[argLen];
        ArgumentParser<?> argumentParser = ArgumentParserRegistry.getParser(parameter.getType());

        if (argumentParser == null) {
            return completion;
        }

        return argumentParser.tabComplete(sender, subCommandArgs[argLen - 1], parameter);
    }
}
