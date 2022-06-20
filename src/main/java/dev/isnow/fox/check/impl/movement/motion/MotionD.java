package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.BlockUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@CheckInfo(name = "Motion", type = "D", description = "Checks for invalid motion in/on liquids.")
public final class MotionD extends Check {

    public MotionD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            if (PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                return;
            }

            if (data.getPlayer().getLocation().subtract(0, 0.1, 0).getBlock() == null) {
                return;
            }
            if (BlockUtil.isLiquid(data.getPlayer().getLocation().subtract(0, 0.1, 0).getBlock())
                    && !BlockUtil.isLiquid(data.getPlayer().getLocation().clone().add(0, 0.2, 0).getBlock())
                    && !data.getVelocityProcessor().isTakingVelocity()
                    && data.getPositionProcessor().getBlocksBelow().stream().noneMatch(block -> block.getType() == Material.WATER_LILY)
                    && data.getPositionProcessor().getWebTicks() == 0) {


                if (!data.getPositionProcessor().isOnGround() && increaseBuffer() > 15 && !isExempt(ExemptType.FLYING)) {
                    fail("DeltaY: " + data.getPositionProcessor().getDeltaY());
                } else {
                    decreaseBufferBy(0.5);
                }
            }

            final double deltaY = data.getPositionProcessor().getDeltaY();

            final List<Block> blocks = data.getPositionProcessor().getBlocks();
            final List<Block> blocksBelow = data.getPositionProcessor().getBlocksBelow();
            final List<Block> blocksAbove = data.getPositionProcessor().getBlocksAbove();

            if (blocks == null || blocksBelow == null || blocksAbove == null) return;

            final boolean liquidBelow = blocksBelow.stream().allMatch(Block::isLiquid);
            final boolean noLiquidAbove = blocksAbove.stream().noneMatch(Block::isLiquid);
            final boolean noBlocks = blocks.stream().anyMatch(block -> block.getType().isSolid());

            final boolean fullySubmerged = data.getPositionProcessor().isFullySubmergedInLiquidStat();

            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.VEHICLE, ExemptType.VELOCITY, ExemptType.FLYING, ExemptType.UNDERBLOCK, ExemptType.CHUNK);
            final boolean invalid = Math.abs(deltaY) < 0.0001 && liquidBelow && noLiquidAbove && !fullySubmerged && !noBlocks;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail("DeltaY: " + data.getPositionProcessor().getDeltaY());
                }
            } else {
                decreaseBuffer();
            }

            final boolean onLiquid = blocksBelow.stream().allMatch(Block::isLiquid);
            final boolean noBlock = blocksBelow.stream().anyMatch(block -> block.getType().isSolid());

            final boolean clientGround = data.getPositionProcessor().isOnGround();
            final boolean serverGround = data.getPositionProcessor().isMathematicallyOnGround();

            final boolean exempt1 = isExempt(ExemptType.BOAT, ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.CHUNK);
            final boolean invalid1 = (clientGround || serverGround) && onLiquid && !noBlock;

            if (invalid1 && !exempt1) {
                if (increaseBuffer() > 5) {
                    fail("DeltaY: " + data.getPositionProcessor().getDeltaY());
                }
            } else {
                decreaseBufferBy(0.50);
            }

            final boolean isFullySubmerged = data.getPositionProcessor().isFullySubmergedInLiquidStat();
            final boolean onGround = data.getPositionProcessor().isOnGround();

            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            final ItemStack boots = data.getPlayer().getInventory().getBoots();

            float f1 = 0.8F;
            float f3;

            if (boots != null) f3 = boots.getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
            else f3 = 0.0F;

            if (f3 > 3.0F) f3 = 3.0F;
            if (!onGround) f3 *= 0.5F;
            if (f3 > 0.0F) f1 += (0.54600006F - f1) * f3 / 3.0F;

            final double predictedX = lastDeltaX * f1 + (sprinting ? 0.0263 : 0.02);
            final double predictedZ = lastDeltaZ * f1 + (sprinting ? 0.0263 : 0.02);

            final double differenceX = deltaX - predictedX;
            final double differenceZ = deltaZ - predictedZ;

            final boolean exempt2 = isExempt(ExemptType.TELEPORT, ExemptType.VEHICLE, ExemptType.FLYING,
                    ExemptType.PISTON, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.WEB,
                    ExemptType.SLIME, ExemptType.BOAT, ExemptType.CHUNK);
            final boolean invalid2 = (differenceX > 0.05 || differenceZ > 0.05) && isFullySubmerged;

            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                return;
            }

            if (invalid2 && !exempt2) {
                if (increaseBuffer() > 2) {
                    fail("DeltaY: " + data.getPositionProcessor().getDeltaY());
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
