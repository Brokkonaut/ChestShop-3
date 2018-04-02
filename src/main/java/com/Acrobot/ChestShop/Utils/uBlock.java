package com.Acrobot.ChestShop.Utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.MaterialData;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Signs.ChestShopSign;

/**
 * @author Acrobot
 */
public class uBlock {
    public static final BlockFace[] CHEST_EXTENSION_FACES = { BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH };
    public static final BlockFace[] SHOP_FACES = { BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH };

    public static Sign getConnectedSign(Block chestBlock) {
        Sign sign = uBlock.findAnyNearbyShopSign(chestBlock);

        if (sign == null) {
            Block neighbour = getNeighbor(chestBlock);
            if (neighbour != null) {
                sign = uBlock.findAnyNearbyShopSign(neighbour);
            }
        }

        return sign;
    }

    private static Block getNeighbor(Block chestBlock) {
        BlockState state = chestBlock.getState();
        if (state instanceof Chest) {
            Chest chest = (Chest) state;
            Inventory inv = chest.getInventory();
            if (inv instanceof DoubleChestInventory) {
                DoubleChestInventory doubleChest = ((DoubleChestInventory) inv);
                Location left = doubleChest.getLeftSide().getLocation();
                if (left != null && !left.equals(chestBlock.getLocation())) {
                    return left.getBlock();
                }
                Location right = doubleChest.getRightSide().getLocation();
                if (right != null && !right.equals(chestBlock.getLocation())) {
                    return right.getBlock();
                }
            }
        }

        return null;
    }

    public static Chest findConnectedChest(Sign sign) {
        Block block = sign.getBlock();
        BlockFace signFace = null;
        MaterialData data = sign.getData();
        if (data instanceof org.bukkit.material.Sign) {
            org.bukkit.material.Sign signData = (org.bukkit.material.Sign) data;
            if (signData.isWallSign()) {
                signFace = signData.getAttachedFace();
                Block faceBlock = block.getRelative(signFace);
                if (BlockUtil.isChest(faceBlock)) {
                    return (Chest) faceBlock.getState();
                }
            }
        }
        for (BlockFace bf : SHOP_FACES) {
            if (bf != signFace) {
                Block faceBlock = block.getRelative(bf);
                if (BlockUtil.isChest(faceBlock)) {
                    return (Chest) faceBlock.getState();
                }
            }
        }
        return null;
    }

    public static Sign findAnyNearbyShopSign(Block block) {
        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = block.getRelative(bf);

            if (!BlockUtil.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();

            if (ChestShopSign.isValid(sign)) {
                return sign;
            }
        }
        return null;
    }

    public static Chest findNeighbor(Block block) {
        for (BlockFace blockFace : CHEST_EXTENSION_FACES) {
            Block neighborBlock = block.getRelative(blockFace);

            if (neighborBlock.getType() == block.getType()) {
                return (Chest) neighborBlock.getState();
            }
        }

        return null;
    }

    private static boolean signIsAttachedToBlock(Sign sign, Block block) {
        return sign.getBlock().equals(block) || BlockUtil.getAttachedBlock(sign).equals(block);
    }
}
