package dev.isnow.fox.check.impl.movement.motion;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "Motion", description = "Checks for invalid jump motion.", type = "H", experimental = true)
public class MotionH extends Check {

    public MotionH(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {

    }
}
