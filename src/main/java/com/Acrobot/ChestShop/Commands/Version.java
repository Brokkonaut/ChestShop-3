package com.Acrobot.ChestShop.Commands;

import com.Acrobot.Breeze.Configuration.Configuration;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.Acrobot.ChestShop.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author Acrobot
 */
public class Version implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!Permission.has(sender, Permission.ADMIN)) {
            sender.sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
            return true;
        }

        if (args.length > 0 && args[0].equals("reload")) {
            Configuration.pairFileAndClass(ChestShop.loadFile("config.yml"), Properties.class);
            Configuration.pairFileAndClass(ChestShop.loadFile("local.yml"), Messages.class);

            sender.sendMessage(Component.text("The config was reloaded.", NamedTextColor.DARK_GREEN));
            return true;
        }

        sender.sendMessage(Component.text(ChestShop.getPluginName() + "'s version is: ", NamedTextColor.GRAY).append(Component.text(ChestShop.getVersion(), NamedTextColor.GREEN)));
        return true;
    }
}
