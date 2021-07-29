package com.Acrobot.ChestShop.Commands;

import com.Acrobot.Breeze.Configuration.Configuration;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import org.bukkit.ChatColor;
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
            NameManager.load();

            sender.sendMessage(ChatColor.DARK_GREEN + "The config was reloaded.");
            return true;
        }
        if (args.length > 0 && args[0].equals("free")) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.DARK_RED + "/chestshop free <username>");
                return true;
            }
            boolean success = NameManager.freeUsername(args[1]);
            if (success) {
                sender.sendMessage(ChatColor.DARK_GREEN + "The name '" + args[1] + "' is now no longer in use.");
            } else {
                sender.sendMessage(ChatColor.DARK_GREEN + "The name '" + args[1] + "' was not in use.");
            }
            return true;
        }

        sender.sendMessage(ChatColor.GRAY + ChestShop.getPluginName() + "'s version is: " + ChatColor.GREEN + ChestShop.getVersion());
        return true;
    }
}
