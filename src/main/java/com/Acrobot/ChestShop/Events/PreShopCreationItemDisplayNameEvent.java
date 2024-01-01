package com.Acrobot.ChestShop.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PreShopCreationItemDisplayNameEvent extends Event {

    private final ItemStack itemStack;
    private String displayName;

    public PreShopCreationItemDisplayNameEvent(ItemStack itemStack, String displayName) {

        this.itemStack = itemStack;
        this.displayName = displayName;
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank())
            throw new IllegalArgumentException("The display name can't be blank or null.");
        this.displayName = displayName;
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
