package dev.isnow.fox.check.impl.movement.noslow;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.PlayerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "NoSlow", type = "D", description = "Checks if player's delta is bigger than expected while blocking a sword.")
public class NoSlowD extends Check {
    public NoSlowD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isPosition()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9)) {
                return;
            }
            debug("D: " + data.getPositionProcessor().getDeltaXZ() + " ED: " + getSpeed(PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED)) + " PL: " + PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED));
            boolean invalid = data.getPositionProcessor().getDeltaXZ() > getSpeed(PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED));
            long seconds = (System.currentTimeMillis() - data.getCombatProcessor().getLastUseEntityPacket()) / 1000;
            if(seconds < 2) {
                return;
            }
            if(invalid && !isExempt(ExemptType.JOINED) && data.getPlayer().isBlocking() && data.getPositionProcessor().isOnGround() && data.getActionProcessor().getBlockingTicks() > 10) {
                fail("DELTAXZ: " + data.getPositionProcessor().getDeltaXZ() + " EXPECTED DELTAXZ: " + getSpeed(PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED)));
            }
        }
    }

    public double getSpeed(int speedLvl) {
        if(data.getPositionProcessor().getAirTicks() >= 2) {
            return 0.123968 + (0.0119 * speedLvl);
        }
        else {
            return 0.073968 + (0.0119 * speedLvl);
        }
    }
}
