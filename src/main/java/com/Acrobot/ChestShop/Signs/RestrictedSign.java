package com.Acrobot.ChestShop.Signs;

import static com.Acrobot.ChestShop.Events.PreTransactionEvent.TransactionOutcome.SHOP_IS_RESTRICTED;
import static com.Acrobot.ChestShop.Permission.ADMIN;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.Acrobot.ChestShop.Permission;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Acrobot
 */
public class RestrictedSign implements Listener {
    private static final BlockFace[] SIGN_CONNECTION_FACES = { BlockFace.SELF, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };

    @EventHandler(ignoreCancelled = true)
    public static void onBlockDestroy(BlockBreakEvent event) {
        Block destroyed = event.getBlock();
        Sign attachedRestrictedSign = getRestrictedSign(destroyed.getLocation());

        if (attachedRestrictedSign == null) {
            return;
        }

        if (!canDestroy(event.getPlayer(), attachedRestrictedSign)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public static void onSignChange(SignChangeEvent event) {
        List<Component> lines = event.lines();
        Player player = event.getPlayer();

        if (isRestricted(lines)) {
            if (!hasPermission(player, lines)) {
                player.sendMessage(Messages.prefix(Messages.ACCESS_DENIED));
                dropSignAndCancelEvent(event);
                return;
            }
            Block connectedSign = event.getBlock().getRelative(BlockFace.DOWN);

            if (!Permission.has(player, ADMIN) || !ChestShopSign.isChestShop(connectedSign)) {
                dropSignAndCancelEvent(event);
                return;
            }

            Sign sign = (Sign) connectedSign.getState();

            if (!ChestShopSign.canAccess(player, sign) && !Permission.has(player, ADMIN)) {
                dropSignAndCancelEvent(event);
            }

            player.sendMessage(Messages.prefix(Messages.RESTRICTED_SIGN_CREATED));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public static void onPreTransaction(PreTransactionEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Sign sign = event.getSign();

        if (isRestrictedShop(sign) && !canAccess(sign, event.getClient())) {
            event.setCancelled(SHOP_IS_RESTRICTED);
        }
    }

    public static Sign getRestrictedSign(Location location) {
        Block currentBlock = location.getBlock();

        if (BlockUtil.isSign(currentBlock)) {
            Sign sign = (Sign) currentBlock.getState();

            if (isRestricted(sign)) {
                return sign;
            } else {
                return null;
            }
        }

        for (BlockFace face : SIGN_CONNECTION_FACES) {
            Block relative = currentBlock.getRelative(face);

            if (!BlockUtil.isSign(relative)) {
                continue;
            }

            Sign sign = (Sign) relative.getState();

            if (!BlockUtil.getAttachedBlock(sign).equals(currentBlock)) {
                continue;
            }

            if (isRestricted(sign)) {
                return sign;
            }
        }

        return null; // No sign found
    }

    public static boolean isRestrictedShop(Sign sign) {
        Block blockUp = sign.getBlock().getRelative(BlockFace.UP);
        return BlockUtil.isSign(blockUp) && isRestricted(((Sign) blockUp.getState()).getSide(Side.FRONT).lines());
    }

    public static boolean isRestricted(@NotNull List<Component> list) {
        return LegacyComponentSerializer.legacySection().serialize(list.get(0)).equalsIgnoreCase("[restricted]");
    }

    public static boolean isRestricted(Sign sign) {
        return isRestricted(sign.getSide(Side.FRONT).lines());
    }

    public static boolean canAccess(Sign sign, Player player) {
        Block blockUp = sign.getBlock().getRelative(BlockFace.UP);
        return !BlockUtil.isSign(blockUp) || hasPermission(player, ((Sign) blockUp.getState()).getSide(Side.FRONT).lines());

    }

    public static boolean canDestroy(Player player, Sign sign) {
        if (Permission.has(player, ADMIN)) {
            return true;
        }

        Sign shopSign = getAssociatedSign(sign);
        return ChestShopSign.canAccess(player, shopSign);
    }

    public static Sign getAssociatedSign(Sign restricted) {
        Block down = restricted.getBlock().getRelative(BlockFace.DOWN);
        return BlockUtil.isSign(down) ? (Sign) down.getState() : null;
    }

    public static boolean hasPermission(Player p, @NotNull List<Component> lines) {
        if (Permission.has(p, ADMIN)) {
            return true;
        }

        for (Component line : lines) {
            if (p.hasPermission(Permission.GROUP.toString() + LegacyComponentSerializer.legacySection().serialize(line))) {
                return true;
            }
        }
        return false;
    }

    private static void dropSignAndCancelEvent(SignChangeEvent event) {
        event.getBlock().breakNaturally();
        event.setCancelled(true);
    }
}
