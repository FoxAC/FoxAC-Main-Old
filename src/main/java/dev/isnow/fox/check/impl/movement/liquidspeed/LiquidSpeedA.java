

package dev.isnow.fox.check.impl.movement.liquidspeed;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "LiquidSpeed", type = "A", description = "Predicts vertical motion in water.")
public final class LiquidSpeedA extends Check {
    public LiquidSpeedA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean inLiquid = data.getPositionProcessor().isFullySubmergedInLiquidStat();

            final double multiplier = data.getPositionProcessor().isInWater() ? 0.8 : 0.5;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double acceleration = deltaY - lastDeltaY;

            final double predicted = lastDeltaY * multiplier - 0.02F;
            final double difference = Math.abs(deltaY - predicted);

            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                return;
            }

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.PISTON,
                    ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.WEB, ExemptType.SLIME, ExemptType.BOAT, ExemptType.CHUNK);
            final boolean invalid = difference > 0.075 && deltaY < -0.075 && acceleration <= 0.0 && inLiquid;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail(difference);
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
