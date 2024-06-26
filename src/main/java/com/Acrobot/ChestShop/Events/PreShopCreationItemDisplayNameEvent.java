package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.ItemNaming.ItemDisplayNameShortener;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Represents an event that is fired before the display name of an item in a shop is created.
 * The final value of displayName will be displayed on the sign of the newly created shop.
 */
public class PreShopCreationItemDisplayNameEvent extends Event {

    private final ItemStack itemStack;
    private String displayName;

    private ItemDisplayNameShortener itemDisplayNameShortener = null;

    public PreShopCreationItemDisplayNameEvent(ItemStack itemStack, String displayName) {

        this.itemStack = itemStack;
        this.displayName = displayName;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemDisplayNameShortener getItemDisplayNameShortener() {
        return itemDisplayNameShortener;
    }

    public void setDisplayName(String displayName) {
        setDisplayName(displayName, null);
    }

    public void setDisplayName(String displayName, ItemDisplayNameShortener itemDisplayNameShortener) {
        if (displayName == null || displayName.isBlank())
            throw new IllegalArgumentException("The display name can't be blank or null.");
        this.displayName = displayName;
        this.itemDisplayNameShortener = itemDisplayNameShortener;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
