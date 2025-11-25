package com.Acrobot.ChestShop.Listeners.Block.Break;

import static com.Acrobot.ChestShop.Permission.ADMIN;
import static com.Acrobot.ChestShop.Permission.MOD;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopDestroyedEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Signs.ChestShopMetaData;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ExplosionResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * @author Acrobot
 */
public class SignBreak implements Listener {
    private final BlockFace[] SIGN_CONNECTION_FACES = { BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP };
    private final HashMap<Location, UUID> signBreakers = new HashMap<>();

    public SignBreak() {
        ChestShop.getPlugin().getServer().getScheduler().runTaskTimer(ChestShop.getPlugin(), this::cleanup, 20, 20);
    }

    public void cleanup() {
        if (!signBreakers.isEmpty()) {
            signBreakers.clear();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSign(BlockPhysicsEvent event) {
        Block block = event.getBlock();

        if (!BlockUtil.isSign(block)) {
            return;
        }

        Sign sign = (Sign) block.getState();
        Block attachedBlock = BlockUtil.getAttachedBlock(sign);

        if (attachedBlock.getType() == Material.AIR && ChestShopSign.isChestShop(sign)) {
            UUID breaker = signBreakers.remove(block.getLocation());
            if (breaker == null) {
                return;
            }
            Player player = Bukkit.getPlayer(breaker);
            if (player == null) {
                return;
            }
            sendShopDestroyedEvent(sign, player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignBreak(BlockBreakEvent event) {
        if (!canBlockBeBroken(event.getBlock(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBrokenSign(BlockBreakEvent event) {
        if (ChestShopSign.isChestShop(event.getBlock())) {
            sendShopDestroyedEvent((Sign) event.getBlock().getState(), event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (!canBlockBeBroken(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (!canBlockBeBroken(block, null)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        if (event.blockList() == null || !Properties.USE_BUILT_IN_PROTECTION || event.getExplosionResult() == ExplosionResult.KEEP || event.getExplosionResult() == ExplosionResult.TRIGGER_BLOCK) {
            return;
        }

        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();
            if (!canBlockBeBroken(block, null)) {
                it.remove();
                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onIgnite(BlockBurnEvent event) {
        if (!canBlockBeBroken(event.getBlock(), null)) {
            event.setCancelled(true);
        }
    }

    public boolean canBlockBeBroken(Block block, Player breaker) {
        List<Sign> attachedSigns = getAttachedSigns(block);
        List<Sign> brokenBlocks = new LinkedList<Sign>();

        boolean canBeBroken = true;

        for (Sign sign : attachedSigns) {

            if (!canBeBroken || !ChestShopSign.isChestShop(sign)) {
                continue;
            }

            if (Properties.TURN_OFF_SIGN_PROTECTION || canDestroyShop(breaker, sign)) {
                brokenBlocks.add(sign);
            } else {
                canBeBroken = false;
            }
        }

        if (!canBeBroken) {
            return false;
        }

        for (Sign sign : brokenBlocks) {
            signBreakers.put(sign.getLocation(), breaker.getUniqueId());
        }

        return true;
    }

    private boolean canDestroyShop(Player player, Sign sign) {
        return player != null && (hasShopBreakingPermission(player) || ChestShopSign.isOwner(player, sign));
    }

    private boolean hasShopBreakingPermission(Player player) {
        return Permission.has(player, ADMIN) || Permission.has(player, MOD);
    }

    private void sendShopDestroyedEvent(Sign sign, Player player) {
        Container connectedChest = null;

        ChestShopMetaData chestShopMetaData = ChestShopSign.getChestShopMetaData(sign);
        if (!chestShopMetaData.isAdminshop()) {
            connectedChest = uBlock.findConnectedChest(sign);
        }

        Event event = new ShopDestroyedEvent(player, sign, connectedChest, chestShopMetaData);
        ChestShop.callEvent(event);
    }

    private List<Sign> getAttachedSigns(Block block) {
        if (block == null) {
            return Lists.newArrayList();
        }

        if (BlockUtil.isSign(block)) {
            return Collections.singletonList((Sign) block.getState());
        } else {
            List<Sign> attachedSigns = new LinkedList<Sign>();

            for (BlockFace face : SIGN_CONNECTION_FACES) {
                Block relative = block.getRelative(face);

                if (!BlockUtil.isSign(relative)) {
                    continue;
                }

                Sign sign = (Sign) relative.getState();

                if (BlockUtil.getAttachedBlock(sign).equals(block)) {
                    attachedSigns.add(sign);
                }
            }

            return attachedSigns;
        }
    }
}
