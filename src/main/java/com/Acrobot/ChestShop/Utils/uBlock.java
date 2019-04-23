package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;

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
        Sign sign = uBlock.findAnyNearbyShopSign(chestBlock, ignoredSign);

        if (sign == null) {
            Block neighbour = getConnectedChest(chestBlock);
            if (neighbour != null) {
                sign = uBlock.findAnyNearbyShopSign(neighbour, ignoredSign);
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

    public static Chest findConnectedChest(Sign sign) {
        Block block = sign.getBlock();
        BlockFace signFace = null;
        BlockData data = sign.getBlockData();
        if (data instanceof WallSign) {
            WallSign signData = (WallSign) data;
            signFace = signData.getFacing().getOppositeFace();
            Block faceBlock = block.getRelative(signFace);
            if (BlockUtil.isChest(faceBlock)) {
                return (Chest) faceBlock.getState();
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

    private static Sign findAnyNearbyShopSign(Block block, Block ignoredSign) {
        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = block.getRelative(bf);

            if ((ignoredSign != null && ignoredSign.equals(faceBlock)) || !BlockUtil.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();
            if (ChestShopSign.isValid(sign)) {
                return sign;
            }
        }
        return null;
    }
}
