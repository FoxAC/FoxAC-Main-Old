package dev.isnow.fox.check.impl.combat.autoblock;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "AutoBlock", type = "B", description = "Checks if player is blocking in a unlikely manner.")
public class AutoBlockC extends Check {

    private boolean block, dig;

    public AutoBlockC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isPosition()) {
            block = dig = false;
        }
        else if(packet.isUseEntity()) {
            WrappedPacketInUseEntity useEntityPacket = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (useEntityPacket.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {

                if (data.getCurrentTicks() < 60) {
                    return;
                }

                if (block || dig) {
                    fail("Dig: "+ dig + " Block: "+ block);
                }
            }
        }
        else if(packet.isBlockDig()) {
            WrappedPacketInBlockDig digPacket = new WrappedPacketInBlockDig(packet.getRawPacket());

            if (digPacket.getDigType() == WrappedPacketInBlockDig.PlayerDigType.RELEASE_USE_ITEM) {
                dig = true;
            }
        }
        else if(packet.isBlockPlace()) {
            block = true;
        }
    }
}
