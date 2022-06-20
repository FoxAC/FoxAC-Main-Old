package dev.isnow.fox.check.impl.movement.flight;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Flight", type = "D", description = "Checks for gravity modifications.")
public final class FlightD extends Check
{
    public FlightD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final int airTicksModifier = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP);
            final int airTicksLimit = 8 + airTicksModifier;

            final int clientAirTicks = data.getPositionProcessor().getClientAirTicks();

            boolean exempt = isExempt(ExemptType.GHOST_BLOCK, ExemptType.LAGGINGHARD, ExemptType.LAGGING, ExemptType.NEARANVIL, ExemptType.NEARSLIME, ExemptType.VELOCITY, ExemptType.PISTON, ExemptType.VEHICLE,
                    ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING,
                    ExemptType.WEB, ExemptType.SLIME, ExemptType.CLIMBABLE);

            final boolean invalid = (clientAirTicks > airTicksLimit) && deltaY > 0.0;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.01);
            }
        }
    }
}