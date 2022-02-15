package dev.isnow.fox.check.impl.combat.hitbox;

import dev.isnow.fox.Fox;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.data.processor.CombatProcessor;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.mc.AxisAlignedBB;
import dev.isnow.fox.util.mc.MathHelper;
import dev.isnow.fox.util.mc.MovingObjectPosition;
import dev.isnow.fox.util.mc.Vec3;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "HitBox", type = "A", description = "Modified Reach / HitBox")
public class HitBoxA extends Check {

    public HitBoxA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntityInteractAt()) {
            WrappedPacketInUseEntity interactAt = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (interactAt.getEntity() instanceof Player) {
                if(!interactAt.getTarget().isPresent()) {
                    return;
                }
                if (Math.abs(interactAt.getTarget().get().x) > 0.400001 || Math.abs(interactAt.getTarget().get().z) > 0.400001) {
                    fail("Missed hitbox.");
                }
            }
        }
        if (packet.isFlying()) {
            final CombatProcessor combatProcessor = data.getCombatProcessor();
            if(combatProcessor.getTarget() == null || combatProcessor.getLastTarget() == null) {
                return;
            }
            if (combatProcessor.getHitTicks() == 1 && combatProcessor.getTarget().getEntityId() == combatProcessor.getLastTarget().getEntityId()) {
                final int totalTicks = Fox.INSTANCE.getTickManager().getTicks();
                final int ticksMS = PacketEvents.get().getPlayerUtils().getPing(data.getPlayer()) / 50;

                final Vector originLoc = new Vector(data.getPositionProcessor().getX(), data.getPositionProcessor().getY(), data.getPositionProcessor().getZ());


                //credits to medusa xD
                double distance = data.getTargetLocations().stream()
                        .filter(pair -> Math.abs(totalTicks - pair.getY() - ticksMS) < 4)
                        .mapToDouble(pair -> {

                            final Vector victimVec = pair.getX().toVector();
                            final AxisAlignedBB targetBox = new AxisAlignedBB(victimVec);

                            Vec3 origin = getPositionEyes(originLoc.getX(),
                                    originLoc.getY(), originLoc.getZ(), data.getPlayer().getEyeHeight());
                            Vec3 look = getVectorForRotation(data.getRotationProcessor().getPitch(), data.getRotationProcessor().getYaw());
                            look = origin.addVector(look.xCoord * 6,
                                    look.yCoord * 6,
                                    look.zCoord * 6);


                            MovingObjectPosition collision = targetBox.calculateIntercept(origin, look);

                            return (collision == null || collision.hitVec == null || look == null) ? victimVec.clone().setY(0).
                                    distance(originLoc.clone().setY(0)) - 0.5f : collision.hitVec.distanceTo(origin) - 0.225f;

                        }).min().orElse(0);


                //don't ask
                if(data.getPositionProcessor().getDeltaXZ() <= 0.01f)
                    distance -= 0.03125f;

                final double bufferIncrease = distance <= 3.05 ? 1 : 1.75;

                if (distance > 3.05) {
                    buffer += bufferIncrease;
                    if (buffer > 3.6) {
                        fail("Reach: " + (float) distance);
                    }
                } else if (buffer > 0 && distance >= 0.185) buffer -= (distance * 0.005);

            }
        }
    }

    public void tickEndEvent() {
        data.getConnectionProcessor().sendTransaction();
    }

    public final Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3((f1 * f2), f3, (f * f2));
    }

    public Vec3 getPositionEyes(final double x, double y, double z, double eyeHeight) {
        return new Vec3(x, y + eyeHeight, z);
    }
}