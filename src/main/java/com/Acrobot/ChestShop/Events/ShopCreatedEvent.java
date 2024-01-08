package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Signs.ChestShopMetaData;
import javax.annotation.Nullable;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a state after shop creation
 *
 * @author Acrobot
 */
public class ShopCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player creator;

    private final Sign sign;
    private final String[] signLines;
    private final ChestShopMetaData chestShopMetaData;
    @Nullable
    private final Container chest;

    public ShopCreatedEvent(Player creator, Sign sign, @Nullable Container chest, String[] signLines, ChestShopMetaData chestShopMetaData) {
        this.creator = creator;
        this.sign = sign;
        this.chest = chest;
        this.signLines = signLines.clone();
        this.chestShopMetaData = chestShopMetaData;
    }

    public boolean isAdminshop() {
        return chestShopMetaData.isAdminshop();
    }

    public ItemStack getItemStack() {
        return chestShopMetaData.getItemStack();
    }

    public int getQuantity() {
        return chestShopMetaData.getQuantity();
    }

    /**
     * Returns the text on the sign
     *
     * @param line
     *            Line number (0-3)
     * @return Text on the sign
     */
    public String getSignLine(short line) {
        return signLines[line];
    }

    /**
     * Returns the text on the sign
     *
     * @return Text on the sign
     */
    public String[] getSignLines() {
        return signLines;
    }

    /**
     * Returns the shop's creator
     *
     * @return Shop's creator
     */
    public Player getPlayer() {
        return creator;
    }

    /**
     * Returns the shop's sign
     *
     * @return Shop's sign
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * Returns the shop's chest (if applicable)
     *
     * @return Shop's chest
     */
    @Nullable
    public Container getChest() {
        return chest;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
