package dev.isnow.fox.check.impl.combat.reach;

import dev.isnow.fox.Fox;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import dev.isnow.fox.util.type.HitboxExpansion;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach", experimental = true, description = "Checks for impossible attack distance.", type = "D")
public final class ReachD extends Check {

    public ReachD(final PlayerData data) {
        super(data);
    }

    public long maxLatency = -1L;

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            Entity target = this.data.getCombatProcessor().getTarget();
            Entity lastTarget = this.data.getCombatProcessor().getLastTarget();

            if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK || this.data.getPlayer().getGameMode() != GameMode.SURVIVAL || !(target instanceof LivingEntity) || target != lastTarget || !this.data.getTargetLocations().isFull() || (long)PlayerUtil.getPing(this.data.getPlayer()) > (this.maxLatency < 0L ? 2147483647L : this.maxLatency)) {
                return;
            }

            int ticks = Fox.INSTANCE.getTickManager().getTicks();
            int pingTicks = NumberConversions.floor((double) PlayerUtil.getPing(this.data.getPlayer()) / 50.0D) + 3;
            Vector player = this.data.getPlayer().getLocation().toVector().setY(0);
            double distance = this.data.getTargetLocations().stream().filter((pair) -> {
                return Math.abs(ticks - pair.getY() - pingTicks) < 3;
            }).mapToDouble((pair) -> {
                Vector victim = pair.getX().toVector().setY(0);
                double expansion = HitboxExpansion.getExpansion(target);
                return player.distance(victim) - expansion;
            }).min().orElse(0.0D);

            debug(distance);

            if (distance > 3.05 && !isExempt(ExemptType.LAGGINGHARD)) {
                if (increaseBuffer() > 3) {
                    fail(distance);
                }
            } else {
                decreaseBufferBy(0.15);
            }
        }
    }
}