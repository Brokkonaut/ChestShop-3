package com.Acrobot.ChestShop.Listeners.PostTransaction;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
import com.Acrobot.ChestShop.ItemNaming.ItemNamingUtils;
import com.Acrobot.ChestShop.UUIDs.NameManager;

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
        Component itemName = parseItemInformation(event.getStock(), event.getAmount());
        String owner = NameManager.getFullNameFor(event.getOwner().getUniqueId());

        Player player = event.getClient();

        if (!event.getOwner().getUniqueId().equals(player.getUniqueId())) {
            String price = Economy.formatBalance(event.getPrice());

            if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
                Component message = formatMessage(Messages.YOU_BOUGHT_FROM_SHOP.replace("%owner", owner), itemName, price);
                player.sendMessage(message);
            }

            if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(event.getOwner())) {
                Component message = formatMessage(Messages.SOMEBODY_BOUGHT_FROM_YOUR_SHOP.replace("%buyer", player.getName()), itemName, price);
                sendMessageToOwner(message, event);
            }
        } else {
            Component message = formatMessage(Messages.YOU_TOOK_FROM_SHOP, itemName, "");
            player.sendMessage(message);
        }
    }

    protected static void sendSellMessage(TransactionEvent event) {
        Component itemName = parseItemInformation(event.getStock(), event.getAmount());
        String owner = NameManager.getFullNameFor(event.getOwner().getUniqueId());

        Player player = event.getClient();

        if (!event.getOwner().getUniqueId().equals(player.getUniqueId())) {
            String price = Economy.formatBalance(event.getPrice());

            if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
                Component message = formatMessage(Messages.YOU_SOLD_TO_SHOP.replace("%buyer", owner), itemName, price);
                player.sendMessage(message);
            }

            if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(event.getOwner())) {
                Component message = formatMessage(Messages.SOMEBODY_SOLD_TO_YOUR_SHOP.replace("%seller", player.getName()), itemName, price);
                sendMessageToOwner(message, event);
            }
        } else {
            Component message = formatMessage(Messages.YOU_PUT_TO_SHOP, itemName, "");
            player.sendMessage(message);
        }
    }

    private static Component parseItemInformation(ItemStack item, int amount) {
        Component message = Component.text(amount + " ");
        String name = ItemNamingUtils.getDisplayName(item);
        TextComponent nameComponent = LegacyComponentSerializer.legacySection().deserialize(name);
        message = message.append(nameComponent).hoverEvent(item);
        return message;
    }

    private static void sendMessageToOwner(Component message, TransactionEvent event) {
        UUID owner = event.getOwner().getUniqueId();

        Player player = Bukkit.getPlayer(owner);

        if (player != null) {
            player.sendMessage(message);
        }
    }

    private static Component formatMessage(String message, Component itemName, String price) {
        message = Messages.prefix(message).replace("%price", price);
        String search = "%item";
        int start = message.indexOf(search);
        if (start < 0) {
            return LegacyComponentSerializer.legacySection().deserialize(message);
        }
        Component messageComponent = LegacyComponentSerializer.legacySection().deserialize(message.substring(0, start));
        messageComponent = messageComponent.append(itemName);
        messageComponent = messageComponent.append(LegacyComponentSerializer.legacySection().deserialize(message.substring(start + search.length())));
        return messageComponent;
    }
}
