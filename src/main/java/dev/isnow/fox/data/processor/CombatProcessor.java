

package dev.isnow.fox.data.processor;

import dev.isnow.fox.Fox;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.util.type.AABB;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.utils.boundingbox.BoundingBox;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class CombatProcessor {

    private final PlayerData data;
    private int hitTicks, swings, hits, currentTargets, attackedHits, attackedTicks;
    private double hitMissRatio, distance;
    private Entity target, lastTarget;
    private long LastUseEntityPacket;
    private long lastAttack, lastAttackTick;
    private final List<AABB> pastVictimBoxes = new ArrayList<>();

    public CombatProcessor(final PlayerData data) {
        this.data = data;
        new BukkitRunnable() {

            @Override
            public void run() {
                if(!data.getPlayer().isOnline()) {
                    cancel();
                }
                if(target != null && target instanceof Player) {
                    if(pastVictimBoxes.size() > 20) pastVictimBoxes.clear();

                    BoundingBox bb = PacketEvents.get().getServerUtils().getEntityBoundingBox(target);
                    pastVictimBoxes.add(new AABB(bb.getMin(), bb.getMax()));
                }
            }
        }.runTaskTimerAsynchronously(Fox.INSTANCE.getPlugin(),0L,1L);
    }

    public void onHitTarget(WrappedPacketInUseEntity packet) {
        if (packet.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
            lastAttack = System.currentTimeMillis();
            this.lastAttackTick = Fox.INSTANCE.getTickManager().getTicks();
        }
    }

    public void handleUseEntity(final WrappedPacketInUseEntity wrapper) {
        LastUseEntityPacket = System.currentTimeMillis();
        if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
            return;
        }
        lastTarget = target == null ? wrapper.getEntity() : target;
        target = wrapper.getEntity();
        if(data.getPlayer().getLocation() != null && target != null && target.getLocation() != null) {
            distance = data.getPlayer().getLocation().toVector().setY(0).distance(target.getLocation().toVector().setY(0)) - .42;
        }

        if(target != null && target instanceof Player && !target.isDead()) {
            PlayerData pd = PlayerDataManager.getInstance().getPlayerData((Player) target);
            if (pd != null) {
                pd.getCombatProcessor().attackedTicks = 0;
                pd.getCombatProcessor().attackedHits++;
            }
        }
        ++hits;

        hitTicks = 0;

        if (target != lastTarget) {
            ++currentTargets;
        }
    }

    public void handleArmAnimation() {
        ++swings;
    }

    public void handleFlying() {
        ++hitTicks;
        ++attackedTicks;
        currentTargets = 0;

        if (swings > 1) {
            hitMissRatio = ((double) hits / (double) swings) * 100;
        }
        if (hits > 100 || swings > 100) {
            hits = swings = 0;
        }
        if(attackedTicks > 20) {
            attackedHits = 0;
        }
    }
}
