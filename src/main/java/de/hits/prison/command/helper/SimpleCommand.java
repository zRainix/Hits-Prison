package de.hits.prison.command.helper;

import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.anno.CustomCommandAnnotation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class SimpleCommand implements CommandExecutor, TabCompleter {
    private final String commandName;

    public SimpleCommand(String commandName) {
        this.commandName = commandName;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(commandName)) {
            return false;
        }

        Method method = findMethod(this.getClass(), "execute");
        if (method == null) {
            sender.sendMessage("§cCommand is not defined.");
            return false;
        }

        CustomCommandAnnotation annotation = method.getAnnotation(CustomCommandAnnotation.class);
        if (annotation != null) {
            if (!hasPermission(sender, annotation.permission()) || !hasOp(sender, annotation.op())) {
                sender.sendMessage("§cYou don't have permission to execute this command.");
                return false;
            }
        }

        Parameter[] parameters = method.getParameters();

        int[] minMaxLength = getMinMaxLength(parameters);

        int minLength = minMaxLength[0];
        int maxLength = minMaxLength[1];

        int argsLength = args.length;

        if (argsLength < minLength || argsLength > maxLength) {
            sender.sendMessage("§cPlease use: §6" + generateCommandHelp(parameters));
            return false;
        }

        try {
            method.setAccessible(true);
            Object[] parsedArgs = parseArgs(sender, parameters, args);
            method.invoke(this, parsedArgs);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§c" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private int[] getMinMaxLength(Parameter[] parameters) {
        int minLen = parameters.length - 1;
        int maxLen = minLen;

        boolean lastNotRequired = false;
        for (int i = parameters.length - 1; i >= 1; i--) {
            Parameter parameter = parameters[i];

            CommandParameter commandParameter = getParameterAnnotation(parameter);

            if (commandParameter == null) {
                break;
            }

            if (commandParameter.required()) {
                break;
            }

            minLen--;
        }

        return new int[]{minLen, maxLen};
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completion = List.of();

        Method method = findMethod(this.getClass(), "execute");
        if (method == null) {
            return completion;
        }

        CustomCommandAnnotation annotation = method.getAnnotation(CustomCommandAnnotation.class);
        if (annotation != null) {
            if (!hasPermission(sender, annotation.permission()) || !hasOp(sender, annotation.op())) {
                return completion;
            }
        }

        int argLen = args.length;

        int paramIndex = argLen;

        method.setAccessible(true);
        Parameter[] parameters = method.getParameters();

        if (!(paramIndex < parameters.length)) {
            return completion;
        }

        Parameter parameter = parameters[paramIndex];
        ArgumentParser argumentParser = ArgumentParserRegistry.getParser(parameter.getType());

        if (argumentParser == null) {
            return completion;
        }

        return argumentParser.tabComplete(sender, args[argLen - 1], parameter);
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return permission.isEmpty() || sender.hasPermission(permission);
    }

    private boolean hasOp(CommandSender sender, boolean requireOp) {
        return !requireOp || sender.isOp();
    }

    private Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    private Object[] parseArgs(CommandSender sender, Parameter[] parameters, String[] args) throws IllegalArgumentException {
        Object[] parsedArgs = new Object[parameters.length];

        Parameter executor = parameters[0];
        if (executor.getType() == CommandSender.class) {
            parsedArgs[0] = sender;
        } else if (executor.getType() == Player.class) {
            if (sender instanceof Player) {
                parsedArgs[0] = (Player) sender;
            } else {
                throw new IllegalArgumentException("§cThis command can only be executed by a player.");
            }
        } else {
            throw new IllegalArgumentException("§cFirst parameter must be CommandSender or Player.");
        }

        for (int i = 1; i < parameters.length; i++) {

            int argsIndex = i - 1;

            Parameter parameter = parameters[i];
            Class<?> parameterClass = parameter.getType();
            CommandParameter parameterAnnotation = parameter.isAnnotationPresent(CommandParameter.class) ? parameter.getAnnotation(CommandParameter.class) : null;

            if (argsIndex >= args.length) {
                if (parameterAnnotation != null && !parameterAnnotation.required()) {
                    parsedArgs[i] = null;
                    continue;
                } else {
                    throw new IllegalArgumentException("§cPlease use: §6" + generateCommandHelp(parameters));
                }
            }

            ArgumentParser<?> parser = ArgumentParserRegistry.getParser(parameterClass);
            if (parser == null) {
                throw new IllegalArgumentException("§cNo parser found for type: §6" + parameterClass.getSimpleName());
            }

            parsedArgs[i] = parser.parse(sender, args[argsIndex], parameter);
        }
        return parsedArgs;
    }

    public String generateCommandHelp(Parameter[] parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(this.commandName);

        for (int i = 1; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            CommandParameter commandParameter = getParameterAnnotation(parameter);

            if (commandParameter == null) {
                String name = parameter.getName();
                String type = parameter.getType().getSimpleName();
                sb.append(" ");
                sb.append("<").append(name).append("(").append(type).append(")").append(">");
            } else {
                String name = commandParameter.name();
                String type = parameter.getType().getSimpleName();
                boolean required = commandParameter.required();
                sb.append(" ");

                sb.append(required ? "<" : "[");
                if (name.isEmpty()) {
                    name = parameter.getName();
                    sb.append(name).append("(").append(type).append(")");
                } else {
                    sb.append(name);
                }
                sb.append(required ? ">" : "]");
            }
        }

        return sb.toString();
    }

    private CommandParameter getParameterAnnotation(Parameter parameter) {
        for (Annotation annotation : parameter.getDeclaredAnnotations()) {
            if (annotation instanceof CommandParameter) {
                return (CommandParameter) annotation;
            }
        }
        return null;
    }
}
