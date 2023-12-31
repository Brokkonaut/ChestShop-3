package com.Acrobot.ChestShop.Signs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import com.Acrobot.Breeze.Utils.PriceUtil;

public class ChestShopMetaData implements ConfigurationSerializable {

    private UUID owner;
    private Set<UUID> accessors;

    private int quantity;

    private double buyPrice;

    private double sellPrice;

    private ItemStack itemStack;

    public ChestShopMetaData(UUID owner, int quantity, double sellPrice, double buyPrice, ItemStack itemStack) {
        this(owner, quantity, sellPrice, buyPrice, itemStack, new HashSet<>());
    }

    private ChestShopMetaData(UUID owner, int quantity, double sellPrice, double buyPrice, ItemStack itemStack, Set<UUID> accessors) {
        this.owner = owner;
        this.quantity = Math.max(1, quantity);
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.itemStack = itemStack;
        this.accessors = accessors;
    }

    public HashSet<UUID> getAccessors() {
        return new HashSet<>(accessors);
    }

    public void addAccessor(UUID uuid) {
        accessors.add(uuid);
    }

    public void removeAccessor(UUID uuid) {
        accessors.remove(uuid);
    }

    public boolean isAccessor(UUID uuid) {
        return accessors.contains(uuid);
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean doesBuy() {
        return buyPrice != PriceUtil.NO_PRICE;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public boolean doesSell() {
        return sellPrice != PriceUtil.NO_PRICE;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("owner", owner);
        data.put("accessors", accessors);
        data.put("amount", quantity);
        data.put("buyPrice", buyPrice);
        data.put("sellPrice", sellPrice);
        data.put("itemStack", itemStack.serialize());
        return data;
    }

    public static ChestShopMetaData deserialize(Map<String, Object> map) {

        UUID owner = (UUID) map.get("owner");
        int amount = (int) map.get("amount");
        double sellPrice = (double) map.get("sellPrice");
        double buyPrice = (double) map.get("buyPrice");
        ItemStack itemStack = (ItemStack) map.get("itemStack");
        Set<UUID> accessors = new HashSet<>((HashSet<UUID>) map.get("accessors"));
        return new ChestShopMetaData(owner, amount, sellPrice, buyPrice, itemStack, accessors);
    }
}
