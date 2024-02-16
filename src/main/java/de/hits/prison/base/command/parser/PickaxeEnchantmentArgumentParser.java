package de.hits.prison.base.command.parser;

import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.helper.ArgumentParser;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Component
public class PickaxeEnchantmentArgumentParser extends ArgumentParser<PickaxeUtil.PickaxeEnchantment> {

    @Autowired
    private static PickaxeUtil pickaxeUtil;

    public PickaxeEnchantmentArgumentParser() {
        super(PickaxeUtil.PickaxeEnchantment.class);
    }

    @Override
    public PickaxeUtil.PickaxeEnchantment parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException {
        PickaxeUtil.PickaxeEnchantment pickaxeEnchantment = pickaxeUtil.getPickaxeEnchantment(arg);
        if (pickaxeEnchantment == null) {
            throw new IllegalArgumentException("§cEnchantment not found: §6" + arg);
        }
        return pickaxeEnchantment;
    }

    @Override
    public String format(PickaxeUtil.PickaxeEnchantment value) {
        return value.getName();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        List<String> completions = new ArrayList<>();
        for (PickaxeUtil.PickaxeEnchantment pickaxeEnchantment : pickaxeUtil.getPickaxeEnchantments()) {
            if (pickaxeEnchantment.getName().toLowerCase().startsWith(arg.toLowerCase())) {
                completions.add(pickaxeEnchantment.getName());
            }
        }
        return completions;
    }
}