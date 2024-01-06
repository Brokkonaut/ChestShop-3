package com.Acrobot.ChestShop;

import com.Acrobot.Breeze.Configuration.Configuration;
import com.Acrobot.ChestShop.Commands.AddAccessor;
import com.Acrobot.ChestShop.Commands.Give;
import com.Acrobot.ChestShop.Commands.ItemInfo;
import com.Acrobot.ChestShop.Commands.RemoveAccessor;
import com.Acrobot.ChestShop.Commands.Toggle;
import com.Acrobot.ChestShop.Commands.Version;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Database.Migrations;
import com.Acrobot.ChestShop.Listeners.AuthMeChestShopListener;
import com.Acrobot.ChestShop.Listeners.Block.BlockPlace;
import com.Acrobot.ChestShop.Listeners.Block.Break.ChestBreak;
import com.Acrobot.ChestShop.Listeners.Block.Break.SignBreak;
import com.Acrobot.ChestShop.Listeners.Block.SignCreate;
import com.Acrobot.ChestShop.Listeners.Economy.ServerAccountCorrector;
import com.Acrobot.ChestShop.Listeners.Economy.TaxModule;
import com.Acrobot.ChestShop.Listeners.GarbageTextListener;
import com.Acrobot.ChestShop.Listeners.Item.ItemMoveListener;
import com.Acrobot.ChestShop.Listeners.ItemInfoListener;
import com.Acrobot.ChestShop.Listeners.Modules.DiscountModule;
import com.Acrobot.ChestShop.Listeners.Modules.PriceRestrictionModule;
import com.Acrobot.ChestShop.Listeners.Player.PlayerConnect;
import com.Acrobot.ChestShop.Listeners.Player.PlayerInteract;
import com.Acrobot.ChestShop.Listeners.Player.PlayerInventory;
import com.Acrobot.ChestShop.Listeners.Player.PlayerLeave;
import com.Acrobot.ChestShop.Listeners.Player.PlayerTeleport;
import com.Acrobot.ChestShop.Listeners.PostShopCreation.CreationFeeGetter;
import com.Acrobot.ChestShop.Listeners.PostShopCreation.MessageSender;
import com.Acrobot.ChestShop.Listeners.PostShopCreation.ShopCreationLogger;
import com.Acrobot.ChestShop.Listeners.PostShopCreation.SignSticker;
import com.Acrobot.ChestShop.Listeners.PostTransaction.EconomicModule;
import com.Acrobot.ChestShop.Listeners.PostTransaction.EmptyShopDeleter;
import com.Acrobot.ChestShop.Listeners.PostTransaction.ItemManager;
import com.Acrobot.ChestShop.Listeners.PostTransaction.TransactionLogger;
import com.Acrobot.ChestShop.Listeners.PostTransaction.TransactionMessageSender;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.ChestChecker;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.ItemChecker;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.MoneyChecker;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.NameChecker;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.PriceChecker;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.PriceRatioChecker;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.QuantityChecker;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.TerrainChecker;
import com.Acrobot.ChestShop.Listeners.PreTransaction.AmountAndPriceChecker;
import com.Acrobot.ChestShop.Listeners.PreTransaction.CreativeModeIgnorer;
import com.Acrobot.ChestShop.Listeners.PreTransaction.ErrorMessageSender;
import com.Acrobot.ChestShop.Listeners.PreTransaction.PartialTransactionModule;
import com.Acrobot.ChestShop.Listeners.PreTransaction.PermissionChecker;
import com.Acrobot.ChestShop.Listeners.PreTransaction.PriceValidator;
import com.Acrobot.ChestShop.Listeners.PreTransaction.ShopValidator;
import com.Acrobot.ChestShop.Listeners.PreTransaction.SpamClickProtector;
import com.Acrobot.ChestShop.Listeners.PreTransaction.StockFittingChecker;
import com.Acrobot.ChestShop.Listeners.ShopRemoval.ShopRefundListener;
import com.Acrobot.ChestShop.Listeners.ShopRemoval.ShopRemovalLogger;
import com.Acrobot.ChestShop.Logging.FileFormatter;
import com.Acrobot.ChestShop.Metadata.ItemDatabase;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Signs.RestrictedSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main file of the plugin
 *
 * @author Acrobot
 */
public class ChestShop extends JavaPlugin {
    private static ChestShop plugin;
    private static Server server;
    private static PluginDescriptionFile description;

    private static File dataFolder;
    private static ItemDatabase itemDatabase;

    private static Logger logger;
    private FileHandler handler;

    public ChestShop() {
        dataFolder = getDataFolder();
        logger = getLogger();
        description = getDescription();
        server = getServer();
        plugin = this;
    }

    @Override
    public void onLoad() {
        ChestShopSign.createNamespacedKeys(this); // Initialize NamespacedKeys
        Dependencies.initializePluginsOnLoad();
    }

    @Override
    public void onEnable() {
        Configuration.pairFileAndClass(loadFile("config.yml"), Properties.class);
        Configuration.pairFileAndClass(loadFile("local.yml"), Messages.class);

        itemDatabase = new ItemDatabase();

        handleMigrations();

        NameManager.load();

        Dependencies.loadPlugins();

        registerEvents();

        if (Properties.LOG_TO_FILE) {
            File log = loadFile("ChestShop.log");

            FileHandler handler = loadHandler(log.getAbsolutePath());
            handler.setFormatter(new FileFormatter());

            this.handler = handler;
            logger.addHandler(handler);
        }

        if (!Properties.LOG_TO_CONSOLE) {
            logger.setUseParentHandlers(false);
        }

        getCommand("iteminfo").setExecutor(new ItemInfo());
        getCommand("csVersion").setExecutor(new Version());
        getCommand("csGive").setExecutor(new Give());
        getCommand("cstoggle").setExecutor(new Toggle());
        getCommand("csAddAccessor").setExecutor(new AddAccessor());
        getCommand("csRemoveAccessor").setExecutor(new RemoveAccessor());
    }

    private void handleMigrations() {
        File versionFile = loadFile("version");
        YamlConfiguration previousVersion = YamlConfiguration.loadConfiguration(versionFile);

        if (previousVersion.get("version") == null) {
            previousVersion.set("version", Migrations.CURRENT_DATABASE_VERSION);

            try {
                previousVersion.save(versionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int lastVersion = previousVersion.getInt("version");
        int newVersion = Migrations.migrate(lastVersion);

        if (lastVersion != newVersion) {
            previousVersion.set("version", newVersion);

            try {
                previousVersion.save(versionFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        boolean requireItemDatabaseUpgrade = false;
        String oldItemStackEncoded = previousVersion.getString("itemstack");
        try {
            String newItemStackEncoded = itemDatabase.encodeItemStack(new ItemStack(Material.DIRT, 1));
            if (!newItemStackEncoded.equals(oldItemStackEncoded)) {
                previousVersion.set("itemstack", newItemStackEncoded);
                previousVersion.save(versionFile);
                requireItemDatabaseUpgrade = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (requireItemDatabaseUpgrade) {
            getLogger().info("Upgrading item database. This may take some time.");
            itemDatabase.upgrade(oldItemStackEncoded == null);
            getLogger().info("Upgrading item database completed.");
        }
    }

    public static File loadFile(String string) {
        File file = new File(dataFolder, string);

        return loadFile(file);
    }

    private static File loadFile(File file) {
        if (!file.exists()) {
            try {
                if (file.getParent() != null) {
                    file.getParentFile().mkdirs();
                }

                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    private static FileHandler loadHandler(String path) {
        FileHandler handler = null;

        try {
            handler = new FileHandler(path, true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return handler;
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);

        Toggle.clearToggledPlayers();

        if (handler != null) {
            handler.close();
            getLogger().removeHandler(handler);
        }
    }

    ////////////////// REGISTER EVENTS, SCHEDULER & STATS ///////////////////////////
    private void registerEvents() {
        registerEvent(new com.Acrobot.ChestShop.Plugins.ChestShop()); // Chest protection

        registerPreShopCreationEvents();
        registerPreTransactionEvents();
        registerPostShopCreationEvents();
        registerPostTransactionEvents();
        registerShopRemovalEvents();

        registerModules();

        registerEvent(new SignBreak());
        registerEvent(new SignCreate());
        registerEvent(new ChestBreak());

        registerEvent(new BlockPlace());
        registerEvent(new PlayerConnect());
        registerEvent(new PlayerInteract());
        registerEvent(new PlayerInventory());
        registerEvent(new PlayerLeave());
        registerEvent(new PlayerTeleport());

        registerEvent(new ItemInfoListener());
        registerEvent(new GarbageTextListener());

        if (this.getServer().getPluginManager().getPlugin("AuthMe") != null && this.getServer().getPluginManager().getPlugin("ChestShop").isEnabled()) {
            registerEvent(new AuthMeChestShopListener());
        }

        registerEvent(new RestrictedSign());

        if (!Properties.TURN_OFF_HOPPER_PROTECTION) {
            registerEvent(new ItemMoveListener());
        }
    }

    private void registerShopRemovalEvents() {
        registerEvent(new ShopRefundListener());
        registerEvent(new ShopRemovalLogger());
    }

    private void registerPreShopCreationEvents() {
        if (Properties.BLOCK_SHOPS_WITH_SELL_PRICE_HIGHER_THAN_BUY_PRICE) {
            registerEvent(new PriceRatioChecker());
        }

        registerEvent(new ChestChecker());
        registerEvent(new MoneyChecker());
        registerEvent(new NameChecker());
        registerEvent(new com.Acrobot.ChestShop.Listeners.PreShopCreation.PermissionChecker());
        registerEvent(new com.Acrobot.ChestShop.Listeners.PreShopCreation.ErrorMessageSender());
        registerEvent(new PriceChecker());
        registerEvent(new QuantityChecker());
        registerEvent(new ItemChecker());
        registerEvent(new TerrainChecker());
    }

    private void registerPostShopCreationEvents() {
        registerEvent(new CreationFeeGetter());
        registerEvent(new MessageSender());
        registerEvent(new SignSticker());
        registerEvent(new ShopCreationLogger());
    }

    private void registerPreTransactionEvents() {
        if (Properties.ALLOW_PARTIAL_TRANSACTIONS) {
            registerEvent(new PartialTransactionModule());
        } else {
            registerEvent(new AmountAndPriceChecker());
        }

        registerEvent(new CreativeModeIgnorer());
        registerEvent(new ErrorMessageSender());
        registerEvent(new PermissionChecker());
        registerEvent(new PriceValidator());
        registerEvent(new ShopValidator());
        registerEvent(new SpamClickProtector());
        registerEvent(new StockFittingChecker());
    }

    private void registerPostTransactionEvents() {
        registerEvent(new EconomicModule());
        registerEvent(new EmptyShopDeleter());
        registerEvent(new ItemManager());
        registerEvent(new TransactionLogger());
        registerEvent(new TransactionMessageSender());
    }

    private void registerModules() {
        registerEvent(new DiscountModule());
        registerEvent(new PriceRestrictionModule());

        registerEconomicalModules();
    }

    private void registerEconomicalModules() {
        registerEvent(new ServerAccountCorrector());
        registerEvent(new TaxModule());
    }

    public void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    ///////////////////////////////////////////////////////////////////////////////

    public static ItemDatabase getItemDatabase() {
        return itemDatabase;
    }

    public static File getFolder() {
        return dataFolder;
    }

    public static Logger getBukkitLogger() {
        return logger;
    }

    public static Server getBukkitServer() {
        return server;
    }

    public static String getVersion() {
        return description.getVersion();
    }

    public static String getPluginName() {
        return description.getName();
    }

    public static List<String> getDependencies() {
        return description.getSoftDepend();
    }

    public static ChestShop getPlugin() {
        return plugin;
    }

    public static void registerListener(Listener listener) {
        plugin.registerEvent(listener);
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }
}
