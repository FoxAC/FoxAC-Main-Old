package dev.isnow.fox.check.impl.combat.reach;

import dev.isnow.fox.Fox;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import dev.isnow.fox.util.type.HitboxExpansion;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach", experimental = true, description = "Checks for impossible attack distance.", type = "C")
public final class ReachC extends Check {

    public ReachC(final PlayerData data) {
        super(data);
    }

    public int hits;

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if(data.getCombatProcessor().getHitTicks() > 3) {
                hits = 0;
            }
        }
        if (packet.isUseEntity()) {
            hits++;
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();

            if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK
                    || data.getPlayer().getGameMode() != GameMode.SURVIVAL
                    || !(target instanceof LivingEntity)
                    || target != lastTarget
                    || !data.getTargetLocations().isFull()
            ) return;

            final int ticks = Fox.INSTANCE.getTickManager().getTicks();
            final int pingTicks = NumberConversions.floor(PlayerUtil.getPing(data.getPlayer()) / 50.0) + 3;

            final Vector player = data.getPlayer().getLocation().toVector().setY(0);

            final double distance = data.getTargetLocations().stream()
                    .filter(pair -> Math.abs(ticks - pair.getY() - pingTicks) < 3)
                    .mapToDouble(pair -> {
                        final Vector victim = pair.getX().toVector().setY(0);
                        final double expansion = HitboxExpansion.getExpansion(target);
                        return player.distance(victim) - expansion;
                    }).min().orElse(0);

            debug(distance);

            if(distance > 3.01) {
                if(increaseBufferBy(0.90) > 2) {
                    fail(distance);
                    resetBuffer();
                }
            }
            else {
                decreaseBufferBy(0.05);
            }
        }
    }
}