package com.Acrobot.ChestShop.Events;

import com.Acrobot.ChestShop.Signs.ChestShopSign;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a state before shop is created
 *
 * @author Acrobot
 */
public class PreShopCreationEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player creator;

    private CreationOutcome outcome = CreationOutcome.SHOP_CREATED_SUCCESSFULLY;
    private Sign sign;
    private String[] signLines;
    private final ItemStack itemStack;
    private UUID shopOwnerId;

    private int amount;
    private boolean enforceAmount;
    private boolean noAutofill;

    public PreShopCreationEvent(Player creator, Sign sign, String[] signLines, ItemStack itemStack) {
        this.creator = creator;
        this.shopOwnerId = this.creator.getUniqueId();
        this.sign = sign;
        this.signLines = signLines.clone();
        this.itemStack = itemStack;
        if (itemStack == null) {
            outcome = CreationOutcome.INVALID_ITEM;
        }
    }

    public String getQuantityLine() {
        return signLines[ChestShopSign.QUANTITY_LINE];
    }

    public void setQuantityLine(String line) {
        signLines[ChestShopSign.QUANTITY_LINE] = Objects.requireNonNull(line);
    }

    public String setItemLine(String line) {
        return signLines[ChestShopSign.ITEM_LINE] = line;
    }

    public ItemStack getItemStack() {

        if (itemStack == null) {
            return null;
        }

        return itemStack.clone();
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isEnforceAmount() {
        return enforceAmount;
    }

    public void setEnforceAmount(boolean enforceAmount) {
        this.enforceAmount = enforceAmount;
    }

    public boolean isNoAutofill() {
        return noAutofill;
    }

    public void setNoAutofill(boolean noAutofill) {
        this.noAutofill = noAutofill;
    }

    /**
     * Returns if event is cancelled
     *
     * @return Is event cancelled?
     */
    public boolean isCancelled() {
        return outcome != CreationOutcome.SHOP_CREATED_SUCCESSFULLY;
    }

    /**
     * Returns the outcome of the event
     *
     * @return Event's outcome
     */
    public CreationOutcome getOutcome() {
        return outcome;
    }

    /**
     * Sets the event's outcome
     *
     * @param outcome
     *            Outcome
     */
    public void setOutcome(CreationOutcome outcome) {
        this.outcome = outcome;
    }

    /**
     * Sets the shop's creator
     *
     * @param creator
     *            Shop's creator
     */
    public void setCreator(Player creator) {
        this.creator = creator;
    }

    /**
     * Sets the sign attached to the shop
     *
     * @param sign
     *            Shop sign
     */
    public void setSign(Sign sign) {
        this.sign = sign;
    }

    /**
     * Sets the text on the sign
     *
     * @param signLines
     *            Text to set
     */
    public void setSignLines(String[] signLines) {
        this.signLines = signLines;
    }

    /**
     * Sets one of the lines on the sign
     *
     * @param line
     *            Line number to set (0-3)
     * @param text
     *            Text to set
     */
    public void setSignLine(byte line, String text) {
        this.signLines[line] = text;
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
     * Returns the text on the sign
     *
     * @param line
     *            Line number (0-3)
     * @return Text on the sign
     */
    public String getSignLine(byte line) {
        return signLines[line];
    }

    public String getOwnerName() {
        return getSignLine((byte) 0);
    }

    public void setOwnerName(String name) {
        this.setSignLine((byte) 0, name);
    }

    public UUID getShopOwnerId() {
        return shopOwnerId;
    }

    public void setShopOwnerId(UUID shopOwnerId) {
        this.shopOwnerId = shopOwnerId;
    }

    /**
     * Returns the text on the sign
     *
     * @return Text on the sign
     */
    public String[] getSignLines() {
        return signLines;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Possible outcomes
     */
    public static enum CreationOutcome {
        INVALID_ITEM,
        INVALID_PRICE,
        INVALID_QUANTITY,
        PLAYER_NOT_FOUND,

        SELL_PRICE_HIGHER_THAN_BUY_PRICE,

        NO_CHEST,

        NO_PERMISSION,
        NO_PERMISSION_FOR_TERRAIN,
        NO_PERMISSION_FOR_CHEST,

        NOT_ENOUGH_MONEY,

        /**
         * For plugin use
         */
        OTHER,

        SHOP_CREATED_SUCCESSFULLY,
    }
}
