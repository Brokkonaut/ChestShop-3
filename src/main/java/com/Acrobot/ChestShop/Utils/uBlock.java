package com.Acrobot.ChestShop.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Signs.ChestShopSign;

/**
 * @author Acrobot
 */
public class uBlock {
    public static final BlockFace[] CHEST_EXTENSION_FACES = { BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH };
    public static final BlockFace[] SHOP_FACES = { BlockFace.SELF, BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH };
    public static final BlockFace[] NEIGHBOR_FACES = { BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH };

    public static Sign getConnectedSign(Block chestBlock) {
        Sign sign = uBlock.findAnyNearbyShopSign(chestBlock);

        if (sign == null && getNeighbor(chestBlock) != null) {
            sign = uBlock.findAnyNearbyShopSign(getNeighbor(chestBlock));
        }

        return sign;
    }

    private static Block getNeighbor(Block chestBlock) {
        Material type = chestBlock.getType();
        for (BlockFace chestFace : NEIGHBOR_FACES) {
            Block relative = chestBlock.getRelative(chestFace);

            if (relative.getType() == type) {
                return relative;
            }
        }

        return null;
    }

    public static Chest findConnectedChest(Sign sign) {
        Block block = sign.getBlock();
        return findConnectedChest(block);
    }

    public static Chest findConnectedChest(Block block) {
        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = block.getRelative(bf);
            if (BlockUtil.isChest(faceBlock)) {
                return (Chest) faceBlock.getState();
            }
        }
        return null;
    }

    public static Sign findValidShopSign(Block block, String originalName) {
        Sign ownerShopSign = null;

        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = block.getRelative(bf);

            if (!BlockUtil.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();

            if (ChestShopSign.isValid(sign) && signIsAttachedToBlock(sign, block)) {
                if (!sign.getLine(0).equals(originalName)) {
                    return sign;
                } else if (ownerShopSign == null) {
                    ownerShopSign = sign;
                }
            }
        }

        return ownerShopSign;
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
