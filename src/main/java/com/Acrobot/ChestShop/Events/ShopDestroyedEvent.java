package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Signs.ChestShopMetaData;
import javax.annotation.Nullable;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a state after shop destruction
 *
 * @author Acrobot
 */
public class ShopDestroyedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player destroyer;

    private final Sign sign;
    private final Container chest;
    private final ChestShopMetaData chestShopMetaData;

    public ShopDestroyedEvent(@Nullable Player destroyer, Sign sign, @Nullable Container chest, ChestShopMetaData chestShopMetaData) {
        this.destroyer = destroyer;
        this.sign = sign;
        this.chest = chest;
        this.chestShopMetaData = chestShopMetaData;
    }

    public ChestShopMetaData getChestShopMetaData() {
        return chestShopMetaData;
    }

    public boolean isAdminshop() {
        return chestShopMetaData.isAdminshop();
    }

    /**
     * @return Shop's destroyer
     */
    @Nullable
    public Player getDestroyer() {
        return destroyer;
    }

    /**
     * @return Shop's chest
     */
    @Nullable
    public Container getChest() {
        return chest;
    }

    /**
     * @return Shop's sign
     */
    public Sign getSign() {
        return sign;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
