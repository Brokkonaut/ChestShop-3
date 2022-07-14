package com.Acrobot.ChestShop.Listeners.PostTransaction;

import java.util.ArrayList;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.ChestShop.Commands.Toggle;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.ComponentUtils;

/**
 * @author Acrobot
 */
public class TransactionMessageSender implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public static void onTransaction(TransactionEvent event) {
        if (event.getTransactionType() == TransactionEvent.TransactionType.BUY) {
            sendBuyMessage(event);
        } else {
            sendSellMessage(event);
        }
    }

    protected static void sendBuyMessage(TransactionEvent event) {
        BaseComponent[] itemName = parseItemInformation(event.getStock());
        String owner = NameManager.getFullNameFor(event.getOwner().getUniqueId());

        Player player = event.getClient();

        if (!event.getOwner().getUniqueId().equals(player.getUniqueId())) {
            String price = Economy.formatBalance(event.getPrice());

            if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
                BaseComponent[] message = formatMessage(Messages.YOU_BOUGHT_FROM_SHOP.replace("%owner", owner), itemName, price);

                player.spigot().sendMessage(message);
            }

            if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(event.getOwner())) {
                BaseComponent[] message = formatMessage(Messages.SOMEBODY_BOUGHT_FROM_YOUR_SHOP.replace("%buyer", player.getName()), itemName, price);

                sendMessageToOwner(message, event);
            }
        } else {
            BaseComponent[] message = formatMessage(Messages.YOU_TOOK_FROM_SHOP, itemName, "");
            player.spigot().sendMessage(message);
        }
    }

    protected static void sendSellMessage(TransactionEvent event) {
        BaseComponent[] itemName = parseItemInformation(event.getStock());
        String owner = NameManager.getFullNameFor(event.getOwner().getUniqueId());

        Player player = event.getClient();

        if (!event.getOwner().getUniqueId().equals(player.getUniqueId())) {
            String price = Economy.formatBalance(event.getPrice());

            if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
                BaseComponent[] message = formatMessage(Messages.YOU_SOLD_TO_SHOP.replace("%buyer", owner), itemName, price);

                player.spigot().sendMessage(message);
            }

            if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(event.getOwner())) {
                BaseComponent[] message = formatMessage(Messages.SOMEBODY_SOLD_TO_YOUR_SHOP.replace("%seller", player.getName()), itemName, price);

                sendMessageToOwner(message, event);
            }
        } else {
            BaseComponent[] message = formatMessage(Messages.YOU_PUT_TO_SHOP, itemName, "");
            player.spigot().sendMessage(message);
        }
    }

    private static BaseComponent[] parseItemInformation(ItemStack item) {
        ArrayList<BaseComponent> message = new ArrayList<>();
        // StringBuilder message = new StringBuilder(15);
        // Joiner joiner = Joiner.on(' ');

        message.add(new TextComponent(item.getAmount() + " "));
        message.add(ComponentUtils.getLocalizedItemName(item.getType()));

        return message.toArray(new BaseComponent[message.size()]);
    }

    private static void sendMessageToOwner(BaseComponent[] message, TransactionEvent event) {
        UUID owner = event.getOwner().getUniqueId();

        Player player = Bukkit.getPlayer(owner);

        if (player != null) {
            player.spigot().sendMessage(message);
        }
    }

    private static BaseComponent[] formatMessage(String message, BaseComponent[] itemName, String price) {
        message = Messages.prefix(message).replace("%price", price);
        String search = "%item";
        int start = message.indexOf(search);
        if (start < 0) {
            return TextComponent.fromLegacyText(message);
        }
        BaseComponent[] messageComponents = TextComponent.fromLegacyText(message.substring(0, start));
        BaseComponent last = messageComponents[messageComponents.length - 1];
        if (itemName.length > 0) {
            last.addExtra(new TextComponent(itemName));
        }
        last.addExtra(new TextComponent(TextComponent.fromLegacyText(message.substring(start + search.length()))));
        return messageComponents;
    }
}
