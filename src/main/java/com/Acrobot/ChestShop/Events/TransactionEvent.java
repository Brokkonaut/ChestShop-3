package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Signs.ChestShopMetaData;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a state after transaction has occured
 *
 * @author Acrobot
 */
public class TransactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final TransactionType type;

    private final Inventory ownerInventory;
    private final Inventory clientInventory;

    private final Player client;
    private final OfflinePlayer owner;

    private final ItemStack stock;
    private final int amount;
    private final double price;

    private final Sign sign;
    private final ChestShopMetaData chestShopMetaData;

    public TransactionEvent(PreTransactionEvent event, Sign sign) {
        this.type = event.getTransactionType();

        this.ownerInventory = event.getOwnerInventory();
        this.clientInventory = event.getClientInventory();

        this.chestShopMetaData = event.getChestShopMetaData();

        this.client = event.getClient();
        this.owner = event.getOwner();

        this.stock = event.getStock();
        this.amount = event.getAmount();
        this.price = event.getPrice();

        this.sign = sign;
    }

    public TransactionEvent(TransactionType type, Inventory ownerInventory, Inventory clientInventory, Player client, OfflinePlayer owner, ItemStack stock, int amount, double price, Sign sign, ChestShopMetaData chestShopMetaData) {
        this.type = type;

        this.ownerInventory = ownerInventory;
        this.clientInventory = clientInventory;

        this.client = client;
        this.owner = owner;

        this.stock = stock;
        this.amount = amount;
        this.price = price;

        this.sign = sign;
        this.chestShopMetaData = chestShopMetaData;
    }

    public ChestShopMetaData getChestShopMetaData() {
        return chestShopMetaData;
    }

    /**
     * @return Type of the transaction
     */
    public TransactionType getTransactionType() {
        return type;
    }

    /**
     * @return Owner's inventory
     */
    public Inventory getOwnerInventory() {
        return ownerInventory;
    }

    /**
     * @return Client's inventory
     */
    public Inventory getClientInventory() {
        return clientInventory;
    }

    /**
     * @return Shop's client
     */
    public Player getClient() {
        return client;
    }

    /**
     * @return Shop's owner
     */
    public OfflinePlayer getOwner() {
        return owner;
    }

    /**
     * @return Stock available
     */
    public ItemStack getStock() {
        return stock;
    }

    public int getAmount() {
        return amount;
    }

    /**
     * @return Total price of the items
     */
    public double getPrice() {
        return price;
    }

    /**
     * @return Shop's sign
     */
    public Sign getSign() {
        return sign;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Possible transaction types
     */
    public enum TransactionType {
        BUY,
        SELL
    }
}
