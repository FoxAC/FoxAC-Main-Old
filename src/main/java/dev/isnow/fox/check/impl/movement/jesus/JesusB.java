

package dev.isnow.fox.check.impl.movement.jesus;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.block.Block;

import java.util.List;

@CheckInfo(name = "Jesus", type = "B", description = "Checks if player is walking on liquids.")
public final class JesusB extends Check {
    public JesusB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                return;
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
                    fail();
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
