package de.hits.prison.base.command.helper;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.anno.BaseCommand;
import de.hits.prison.base.command.anno.CommandParameter;
import de.hits.prison.base.command.anno.SubCommand;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public abstract class SimpleCommand implements CommandExecutor, TabCompleter {

    @Autowired
    private static Logger logger;
    protected final String commandName;

    protected final static String MULTI_VALUE_DELIMITER = ",";
    protected final static String MULTI_VALUE_ALL_STRING = "@a";
    protected final static String MULTI_VALUE_RANDOM_STRING = "@r";

    public SimpleCommand(String commandName) {
        this.commandName = commandName;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase(this.commandName)) {
            return true;
        }

        Method method = findBaseCommand(this.getClass());
        if (method == null) {
            MessageUtil.sendMessage(sender, "§cCommand is not defined.");
            return true;
        }

        BaseCommand annotation = method.getAnnotation(BaseCommand.class);
        if (annotation != null) {
            if (!hasPermission(sender, annotation.permission()) || !hasOp(sender, annotation.op())) {
                MessageUtil.sendMessage(sender, "§cYou don't have permission to execute this command.");
                return true;
            }
        }

        Parameter[] parameters = method.getParameters();

        int[] minMaxLength = getMinMaxLength(parameters);

        int minLength = minMaxLength[0];
        int maxLength = minMaxLength[1];

        int argsLength = args.length;

        if (argsLength < minLength || argsLength > maxLength) {
            MessageUtil.sendMessage(sender, "§cPlease use: §6" + generateCommandHelp(parameters));
            return true;
        }

        try {
            method.setAccessible(true);
            Object[] parsedArgs = parseArgs(sender, parameters, args);
            method.invoke(this, parsedArgs);
        } catch (IllegalArgumentException e) {
            MessageUtil.sendMessage(sender, "§c" + e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while executing command.", e);
        }

        return true;
    }

    protected int[] getMinMaxLength(Parameter[] parameters) {
        int minLen = parameters.length - 1;
        int maxLen = minLen;

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

        Method method = findBaseCommand(this.getClass());
        if (method == null) {
            return completion;
        }

        BaseCommand annotation = method.getAnnotation(BaseCommand.class);
        if (annotation != null) {
            if (!hasPermission(sender, annotation.permission()) || !hasOp(sender, annotation.op())) {
                return completion;
            }
        }

        return tabCompleteMethod(sender, method, args);
    }

    protected List<String> tabCompleteMethod(CommandSender sender, Method method, String[] args) {
        List<String> completion = List.of();

        int argLen = args.length;

        method.setAccessible(true);
        Parameter[] parameters = method.getParameters();

        if (!(argLen < parameters.length)) {
            return completion;
        }

        Parameter parameter = parameters[argLen];
        Class<?> parameterClass = parameter.getType();

        String arg = args[argLen - 1];

        if (parameterClass == List.class) {
            Type listType = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
            ArgumentParser<?> listParser = ArgumentParserRegistry.getParser((Class<?>) listType);
            if (listParser == null)
                throw new IllegalArgumentException("§cNo parser found for type: §6" + parameterClass.getSimpleName());

            String[] splitArgs = arg.split(MULTI_VALUE_DELIMITER);

            int lastIndex = splitArgs.length - 1;

            int commaCount = (int) arg.chars().filter(ch -> ch == ',').count();

            boolean startingWithComma = commaCount != lastIndex;
            List<String> tabComplete = listParser.tabComplete(sender, startingWithComma ? "" : splitArgs[lastIndex], parameter);
            if (commaCount == 0)
                tabComplete.addAll(List.of(MULTI_VALUE_ALL_STRING, MULTI_VALUE_RANDOM_STRING));

            return tabComplete.stream().filter(tab -> !List.of(splitArgs).contains(tab)).map(tab -> {
                if (startingWithComma) {
                    return String.join(MULTI_VALUE_DELIMITER, splitArgs) + MULTI_VALUE_DELIMITER + tab;
                } else {
                    splitArgs[lastIndex] = tab;
                    return String.join(MULTI_VALUE_DELIMITER, splitArgs);
                }
            }).toList();
        } else {
            ArgumentParser<?> argumentParser = ArgumentParserRegistry.getParser(parameter.getType());

            if (argumentParser == null) {
                return completion;
            }

            return argumentParser.tabComplete(sender, arg, parameter);
        }
    }

    protected boolean hasPermission(CommandSender sender, String permission) {
        return permission.isEmpty() || sender.hasPermission(permission);
    }

    protected boolean hasOp(CommandSender sender, boolean requireOp) {
        return !requireOp || sender.isOp();
    }

    protected Method findBaseCommand(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BaseCommand.class)) {
                return method;
            }
        }
        return null;
    }

    protected List<Method> findSubCommands(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(SubCommand.class)).collect(Collectors.toList());
    }

    protected Object[] parseArgs(CommandSender sender, Parameter[] parameters, String[] args) throws IllegalArgumentException {
        return parseArgs(sender, parameters, args, null);
    }

    protected Object[] parseArgs(CommandSender sender, Parameter[] parameters, String[] args, String subCommand) throws IllegalArgumentException {
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

            String arg = args[argsIndex];

            ArgumentParser<?> parser = ArgumentParserRegistry.getParser(parameterClass);

            if (parameterClass == List.class) {
                Type listType = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
                ArgumentParser<?> listParser = ArgumentParserRegistry.getParser((Class<?>) listType);
                if (listParser == null)
                    throw new IllegalArgumentException("§cNo parser found for type: §6" + parameterClass.getSimpleName());

                List<Object> list = new ArrayList<>();

                String[] splitArgs = arg.split(MULTI_VALUE_DELIMITER);

                List<String> argsToParse;

                if (arg.equals(MULTI_VALUE_ALL_STRING)) {
                    argsToParse = listParser.tabComplete(sender, "", parameter);
                } else if (arg.equals(MULTI_VALUE_RANDOM_STRING)) {
                    List<String> shuffle = listParser.tabComplete(sender, "", parameter);
                    Collections.shuffle(shuffle);
                    if (shuffle.isEmpty())
                        throw new IllegalArgumentException("§cCould not get random element. No elements found.");
                    argsToParse = List.of(shuffle.get(0));
                } else {
                    argsToParse = List.of(splitArgs);
                }

                for (String splitArg : argsToParse) {
                    list.add(listParser.parse(sender, splitArg, parameter));
                }

                parsedArgs[i] = list;
            } else {
                if (parser == null) {
                    throw new IllegalArgumentException("§cNo parser found for type: §6" + parameterClass.getSimpleName());
                }
                parsedArgs[i] = parser.parse(sender, arg, parameter);
            }
        }
        return parsedArgs;
    }

    public String generateCommandHelp(Parameter[] parameters) {
        return generateCommandHelp(parameters, null);
    }

    public String generateCommandHelp(Parameter[] parameters, String subCommand) {
        StringBuilder sb = new StringBuilder();

        sb.append("/").append(this.commandName);

        if (subCommand != null) {
            sb.append(" ").append(subCommand);
        }

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

    public String getCommandName() {
        return commandName;
    }

    protected CommandParameter getParameterAnnotation(Parameter parameter) {
        for (Annotation annotation : parameter.getDeclaredAnnotations()) {
            if (annotation instanceof CommandParameter) {
                return (CommandParameter) annotation;
            }
        }
        return null;
    }

    public List<String> getAliases() {
        Method baseCommand = findBaseCommand(this.getClass());
        if (baseCommand == null)
            return new ArrayList<>();
        return List.of(baseCommand.getAnnotation(BaseCommand.class).aliases());
    }
}
