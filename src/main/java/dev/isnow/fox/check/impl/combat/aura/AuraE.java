package dev.isnow.fox.check.impl.combat.aura;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@CheckInfo(name = "Aura", type = "E", description = "Checks for hit occlusion (wallhit).")
public class AuraE extends Check {

    private Location lastAttackerLocation;
    private float lastYaw, lastPitch;

    public AuraE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final Entity target = data.getCombatProcessor().getTarget();
            final Player attacker = data.getPlayer();

            if (target == null || attacker == null) return;
            if (target.getWorld() != attacker.getWorld()) return;

            final Location attackerLocation = attacker.getLocation();

            final float yaw = data.getRotationProcessor().getYaw() % 360F;
            final float pitch = data.getRotationProcessor().getPitch();

            if (lastAttackerLocation != null) {
                final boolean check = yaw != lastYaw &&
                        pitch != lastPitch &&
                        attackerLocation.distance(lastAttackerLocation) > 0.1;

                if (check && !attacker.hasLineOfSight(target)) {
                    if (increaseBuffer() > 20) {
                        fail("pitch=" + pitch);
                    }
                } else {
                    decreaseBuffer();
                }
            }

            lastAttackerLocation = attacker.getLocation();

            lastYaw = yaw;
            lastPitch = pitch;
        }
    }
}
