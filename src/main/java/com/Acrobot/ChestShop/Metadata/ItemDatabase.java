package com.Acrobot.ChestShop.Metadata;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import com.Acrobot.Breeze.Utils.Encoding.Base62;
import com.Acrobot.Breeze.Utils.Encoding.Base64;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Database.DaoCreator;
import com.Acrobot.ChestShop.Database.Item;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.SelectArg;

/**
 * Saves items with Metadata in database, which allows for saving items on signs easily.
 *
 * @author Acrobot
 */
public class ItemDatabase {
    private Dao<Item, Integer> itemDao;

    public ItemDatabase() {
        try {
            itemDao = DaoCreator.getDaoAndCreateTable(Item.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the item code for this item
     *
     * @param item
     *            Item
     * @return Item code for this item
     */
    public String getItemCode(ItemStack item) {
        try {
            ItemStack clone = new ItemStack(item);
            clone.setAmount(1);

            String code = encodeItemStack(clone);
            Item itemEntity = itemDao.queryBuilder().where().eq("code", new SelectArg(code)).queryForFirst();

            if (itemEntity != null) {
                return Base62.encode(itemEntity.getId());
            }

            itemEntity = new Item(code);

            itemDao.create(itemEntity);

            int id = itemEntity.getId();

            return Base62.encode(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets an ItemStack from a item code
     *
     * @param code
     *            Item code
     * @return ItemStack represented by this code
     */
    public ItemStack getFromCode(String code) {
        try {
            int id = Base62.decode(code);
            Item item = itemDao.queryBuilder().where().eq("id", new SelectArg(id)).queryForFirst();

            if (item == null) {
                return null;
            }

            String serialized = item.getBase64ItemCode();

            return decodeItemStack(serialized);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void upgrade(boolean fromLegacy) {
        try {
            List<Item> items = itemDao.queryBuilder().query();
            int modified = 0;
            int mutants = 0;
            int collisions = 0;
            int errors = 0;
            int total = items.size();
            int current = 0;
            for (Item item : items) {
                current++;
                if (current % 100 == 0 || current == total) {
                    ChestShop.getPlugin().getLogger().info("Converting... " + current + "/" + total);
                }
                try {
                    String serialized = item.getBase64ItemCode();
                    String reserialized = encodeItemStack(fromLegacy ? decodeItemStackLegacy(serialized) : decodeItemStack(serialized));
                    if (!serialized.equals(reserialized)) {
                        String reserialized2 = encodeItemStack(decodeItemStack(reserialized));
                        if (!fromLegacy && !reserialized.equals(reserialized2)) {
                            mutants++;
                            ChestShop.getPlugin().getLogger().info("Before:\n" + reserialized);
                            ChestShop.getPlugin().getLogger().info("After:\n" + reserialized2);
                            itemDao.delete(item);
                        } else {
                            Item itemCollision = itemDao.queryBuilder().where().eq("code", new SelectArg(reserialized2)).queryForFirst();
                            if (itemCollision != null && fromLegacy) {
                                itemCollision = null;
                                reserialized2 = encodeItemStack(decodeItemStack(reserialized2), true);
                                collisions++;
                            }
                            if (itemCollision == null) {
                                item.setBase64ItemCode(reserialized2);
                                itemDao.update(item);
                                modified++;
                            } else {
                                collisions++;
                            }
                        }
                    }
                } catch (Throwable e) {
                    errors++;
                    ChestShop.getPlugin().getLogger().log(Level.SEVERE, "Could not upgrade item " + item.getId() + " (" + Base62.encode(item.getId()) + "): " + e.getClass().getSimpleName());
                    itemDao.delete(item);
                }
            }
            ChestShop.getPlugin().getLogger().info("Updated " + modified + "/" + items.size() + " items (" + collisions + " collisions, " + mutants + " mutants, " + errors + " errors)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String encodeItemStack(ItemStack stack) {
        return encodeItemStack(stack, false);
    }

    public String encodeItemStack(ItemStack stack, boolean addRandomValue) {
        Preconditions.checkState(Bukkit.isPrimaryThread(), "Not called from main thread!");
        YamlConfiguration conf = new YamlConfiguration();
        conf.set("item", stack);
        if (addRandomValue) {
            conf.set("random", UUID.randomUUID().toString());
        }
        return java.util.Base64.getEncoder().encodeToString(conf.saveToString().getBytes(Charset.forName("UTF-8")));
    }

    public ItemStack decodeItemStack(String serialized) {
        Preconditions.checkState(Bukkit.isPrimaryThread(), "Not called from main thread!");
        YamlConfiguration conf = new YamlConfiguration();
        try {
            conf.loadFromString(new String(java.util.Base64.getDecoder().decode(serialized), Charset.forName("UTF-8")));
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return new ItemStack(conf.getItemStack("item"));
    }

    public ItemStack decodeItemStackLegacy(String serialized) throws IOException {
        Preconditions.checkState(Bukkit.isPrimaryThread(), "Not called from main thread!");
        try {
            Yaml yaml = new Yaml(new YamlBukkitConstructor(), new YamlRepresenter(), new DumperOptions());
            return new ItemStack(yaml.loadAs((String) Base64.decodeToObject(serialized), ItemStack.class));
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    private class YamlBukkitConstructor extends YamlConstructor {
        public YamlBukkitConstructor() {
            this.yamlConstructors.put(new Tag(Tag.PREFIX + "org.bukkit.inventory.ItemStack"), yamlConstructors.get(Tag.MAP));
        }

    }
}
