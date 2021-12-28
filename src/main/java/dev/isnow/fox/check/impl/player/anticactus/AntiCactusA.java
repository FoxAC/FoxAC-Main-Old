package dev.isnow.fox.check.impl.player.anticactus;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import org.bukkit.Material;

@CheckInfo(name = "AntiCactus", type = "A", description = "So fox would be 0/10 if there would be no anti cactus check so here it is")
public class AntiCactusA extends Check {

    public AntiCactusA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying() && !isExempt(ExemptType.CREATIVE, ExemptType.JOINED, ExemptType.HURT)) {
            if (data.getPlayer().getLocation().add(0, 0, -0.31).getBlock().getType() == Material.CACTUS) {
                fail();
            }
            if (data.getPlayer().getLocation().add(0, 0, 0.31).getBlock().getType() == Material.CACTUS) {
                fail();
            }
            if (data.getPlayer().getLocation().add(0.31, 0, 0).getBlock().getType() == Material.CACTUS) {
                fail();
            }
            if (data.getPlayer().getLocation().add(-0.31, 0, 0).getBlock().getType() == Material.CACTUS) {
                fail();
            }
        }
    }
}
