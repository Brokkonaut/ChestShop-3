package com.Acrobot.ChestShop.Configuration;

import org.bukkit.ChatColor;

import com.Acrobot.Breeze.Configuration.Annotations.PrecededBySpace;

/**
 * @author Acrobot
 */
public class Messages {
    public static String PREFIX = ChatColor.GREEN + "[Shop] " + ChatColor.RESET;
    public static String ITEM_INFO = ChatColor.GREEN + "Item Information: " + ChatColor.RESET;
    public static String SHOP_INFO = ChatColor.GREEN + "Shop Information: " + ChatColor.RESET;
    public static String SHOP_OWNER_INFO = ChatColor.GREEN + "Shop-Owner Information: " + ChatColor.RESET;

    @PrecededBySpace
    public static String ACCESS_DENIED = "You don't have permission to do that!";

    @PrecededBySpace
    public static String NOT_ENOUGH_MONEY = "You don't have enough money!";
    public static String NOT_ENOUGH_MONEY_SHOP = "Shop owner doesn't have enough money!";

    @PrecededBySpace
    public static String CLIENT_DEPOSIT_FAILED = "Money deposit to your account failed!";
    public static String SHOP_DEPOSIT_FAILED = "Money deposit to shop owner failed!";

    @PrecededBySpace
    public static String NO_BUYING_HERE = "You can't buy here!";
    public static String NO_SELLING_HERE = "You can't sell here!";

    @PrecededBySpace
    public static String NOT_ENOUGH_SPACE_IN_INVENTORY = "You haven't got enough space in inventory!";
    public static String NOT_ENOUGH_SPACE_IN_CHEST = "There isn't enough space in chest!";
    public static String NOT_ENOUGH_ITEMS_TO_SELL = "You don't have enough items to sell!";

    @PrecededBySpace
    public static String NOT_ENOUGH_STOCK = "This shop is out of stock.";
    public static String NOT_ENOUGH_STOCK_IN_YOUR_SHOP = "Your %material shop as %location is out of stock!";
    public static String FULL_SHOP_TO_OWNER = "Your %material shop at %location is full!";

    @PrecededBySpace
    public static String YOU_BOUGHT_FROM_SHOP = "You bought %item from %owner for %price.";
    public static String SOMEBODY_BOUGHT_FROM_YOUR_SHOP = "%buyer bought %item for %price from you.";
    public static String YOU_TOOK_FROM_SHOP = "You took %item from your shop.";

    @PrecededBySpace
    public static String YOU_SOLD_TO_SHOP = "You sold %item to %buyer for %price.";
    public static String SOMEBODY_SOLD_TO_YOUR_SHOP = "%seller sold %item for %price to you.";
    public static String YOU_PUT_TO_SHOP = "You put %item into your shop.";

    @PrecededBySpace
    public static String YOU_CANNOT_CREATE_SHOP = "You can't create this type of shop!";
    public static String NO_CHEST_DETECTED = "Couldn't find a chest!";
    public static String INVALID_SHOP_DETECTED = "The shop cannot be used!";
    public static String CANNOT_ACCESS_THE_CHEST = "You don't have permissions to access this chest!";

    @PrecededBySpace
    public static String PROTECTED_SHOP = "Successfully protected the shop with LWC!";
    public static String SHOP_CREATED = "Shop successfully created!";
    public static String SHOP_FEE_PAID = "You have been charged %amount";
    public static String SHOP_REFUNDED = "You have been refunded %amount.";
    public static String ITEM_GIVEN = "Given %item to %player.";

    @PrecededBySpace
    public static String AVAILABLE_ITEMS = "%amount items are available in this shop.";
    public static String AVAILABLE_SPACE = "There is free space for %amount items.";

    @PrecededBySpace
    public static String RESTRICTED_SIGN_CREATED = "Sign successfully created!";

    @PrecededBySpace
    public static String PLAYER_NOT_FOUND = "Player not found!";
    public static String NO_PERMISSION = "You don't have permissions to do that!";
    public static String INCORRECT_ITEM_ID = "You have specified an invalid item id!";
    public static String NOT_ENOUGH_PROTECTIONS = "Could not create a protection!";

    @PrecededBySpace
    public static String CANNOT_CREATE_SHOP_HERE = "You can't create shop here!";

    @PrecededBySpace
    public static String TOGGLE_MESSAGES_OFF = "You will no longer receive messages from your shop(s).";
    public static String TOGGLE_MESSAGES_ON = "You will now receive messages from your shop(s).";

    @PrecededBySpace
    public static String MUST_LOOK_AT_SHOP_SIGN = "You must look at a shop sign when using this command.";
    public static String INVALID_ITEM_LINE = "You have specified an invalid item!";
    public static String INVALID_PRICE_LINE = "You have specified an invalid price!";
    public static String INVALID_AMOUNT_LINE = "You have specified an invalid amount!";
    public static String SHOP_UPDATE_FAILED = "This shop cannot be edited.";

    @PrecededBySpace
    public static String OWNER_CANT_BE_ACCESSOR = "The owner of a shop can't be added as a accessor.";
    public static String NEW_ACCESSOR_ADDED = "New accessor added.";
    public static String ACCESSOR_ALREADY_ADDED = "Accessor already added.";
    public static String ACCESSOR_REMOVED = "Accessor removed.";
    public static String ACCESSOR_NOT_ADDED = "Player isn't a accessor.";

    @PrecededBySpace
    public static String SHOP_ACCESSORS = "Accessors: %accessor_list";

    public static String prefix(String message) {
        return PREFIX + message;
    }
}
