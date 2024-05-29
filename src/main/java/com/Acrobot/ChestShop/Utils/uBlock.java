package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Acrobot
 */
public class uBlock {
    public static final BlockFace[] CHEST_EXTENSION_FACES = { BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH };
    public static final BlockFace[] SHOP_FACES = { BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH };

    public static Sign getConnectedSign(Block chestBlock) {
        return getConnectedSign(chestBlock, null);
    }

    public static Sign getConnectedSign(Block chestBlock, Block ignoredSign) {
        Sign sign = findAnyNearbyShopSign(chestBlock, ignoredSign);

        if (sign == null) {
            Block neighbour = getConnectedChest(chestBlock);
            if (neighbour != null) {
                sign = findAnyNearbyShopSign(neighbour, ignoredSign);
            }
        }

        return sign;
    }

    public static Block getConnectedChest(Block chestBlock) {
        BlockData blockData = chestBlock.getBlockData();
        if (!(blockData instanceof org.bukkit.block.data.type.Chest)) {
            return null;
        }

        org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) blockData;
        if (chestData.getType() != org.bukkit.block.data.type.Chest.Type.SINGLE) {
            BlockFace chestFace = chestData.getFacing();
            // we have to rotate is to get the adjacent chest
            // west, right -> south
            // west, left -> north
            if (chestFace == BlockFace.WEST) {
                chestFace = BlockFace.NORTH;
            } else if (chestFace == BlockFace.NORTH) {
                chestFace = BlockFace.EAST;
            } else if (chestFace == BlockFace.EAST) {
                chestFace = BlockFace.SOUTH;
            } else if (chestFace == BlockFace.SOUTH) {
                chestFace = BlockFace.WEST;
            }
            if (chestData.getType() == org.bukkit.block.data.type.Chest.Type.RIGHT) {
                chestFace = chestFace.getOppositeFace();
            }

            Block face = chestBlock.getRelative(chestFace);

            // They're placing it beside a chest, check if it's already
            // protected
            if (face.getType() == chestBlock.getType()) {
                return face;
            }
        }
        return null;
    }

    public static Container findConnectedChest(Sign sign) {
        return findConnectedChest(sign, false);
    }

    public static Container findConnectedChest(Sign sign, boolean upgradeItems) {
        Block chestBlock = findConnectedChestBlock(sign, upgradeItems);
        return chestBlock == null ? null : (Container) chestBlock.getState();
    }

    public static Block findConnectedChestBlock(Sign sign) {
        return findConnectedChestBlock(sign, false);
    }

    public static Block findConnectedChestBlock(Sign sign, boolean upgradeItems) {
        Block block = sign.getBlock();
        BlockFace signFace = null;
        BlockData data = sign.getBlockData();
        if (data instanceof WallSign) {
            WallSign signData = (WallSign) data;
            signFace = signData.getFacing().getOppositeFace();
            Block faceBlock = block.getRelative(signFace);
            if (BlockUtil.isChest(faceBlock)) {
                // if (upgradeItems) {
                // upgradeContainerItems(faceBlock);
                // }
                return faceBlock;
            }
        }
        for (BlockFace bf : SHOP_FACES) {
            if (bf != signFace) {
                Block faceBlock = block.getRelative(bf);
                if (BlockUtil.isChest(faceBlock)) {
                    // if (upgradeItems) {
                    // upgradeContainerItems(faceBlock);
                    // }
                    return faceBlock;
                }
            }
        }
        return null;
    }

    /**
     * Never call this method! It will destroy itemstacks!
     *
     * @param chestBlock
     */
    @Deprecated(forRemoval = true)
    private static void upgradeContainerItems(Block chestBlock) {
        BlockState blockState = chestBlock.getState();
        if (blockState instanceof Container) {
            Container container = (Container) blockState;
            Inventory inventory = container.getInventory();
            ItemStack[] contents = inventory.getContents();
            YamlConfiguration conf = null;
            for (int i = 0; i < contents.length; i++) {
                ItemStack stack = contents[i];
                if (stack != null && stack.hasItemMeta()) {
                    ItemMeta meta = stack.getItemMeta();
                    if (meta.hasDisplayName() || meta.hasLocalizedName() || meta.hasLore()) {
                        if (conf == null) {
                            conf = new YamlConfiguration();
                        }
                        conf.set("item" + i, stack);
                    }
                }
            }
            if (conf != null) {
                try {
                    conf.loadFromString(conf.saveToString());
                } catch (InvalidConfigurationException e) {
                    throw new RuntimeException("should be impossible", e);
                }
                for (int i = 0; i < contents.length; i++) {
                    ItemStack stack = conf.getItemStack("item" + i);
                    if (stack != null) {
                        contents[i] = stack;
                    }
                }
                inventory.setContents(contents);
            }
        }
    }

    private static Sign findAnyNearbyShopSign(Block block, Block ignoredSign) {
        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = block.getRelative(bf);

            if ((ignoredSign != null && ignoredSign.equals(faceBlock)) || !BlockUtil.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();
            if (ChestShopSign.isChestShop(sign)) {
                Block attachedTo = findConnectedChestBlock(sign);
                if (attachedTo != null && attachedTo.equals(block)) {
                    return sign;
                }
            }
        }
        return null;
    }

    public static boolean isShopChest(Block chest) {
        return getConnectedSign(chest) != null;
    }

    public static boolean isShopChest(InventoryHolder holder) {
        Block block = getInventoryHolderBlock(holder);
        return block != null && isShopChest(block);
    }

    public static Block getInventoryHolderBlock(InventoryHolder holder) {
        if (holder instanceof DoubleChest) {
            return ((DoubleChest) holder).getLocation().getBlock();
        } else if (holder instanceof BlockState) {
            return ((BlockState) holder).getBlock();
        } else {
            return null;
        }
    }
}
