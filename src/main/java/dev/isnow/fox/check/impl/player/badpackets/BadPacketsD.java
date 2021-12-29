package dev.isnow.fox.check.impl.player.badpackets;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.packet.Packet;

@CheckInfo(name = "BadPackets", type = "D", description = "Invalid Game Speed")
public class BadPacketsD extends Check {
    public BadPacketsD(PlayerData data) {
        super(data);
    }

    private int sentFlying;

    private long currentFlying;
    private long balance;
    private double buffer;

    @Override
    public void handle(Packet packet) {
        if (packet.isFlyingType()) {

            sentFlying++;
            long lastFlying = 0;

            if(currentFlying != 0) {
                lastFlying = currentFlying;
            } else {
                currentFlying = System.currentTimeMillis();
                return;
            }
            currentFlying = System.currentTimeMillis();

            balance += 50 - (currentFlying - lastFlying);

            if (balance > 8) {
                if (buffer++ > 1  && sentFlying > 100) {
                    fail();
                    buffer = 0;
                }
                balance = 0;
            } else {
                buffer = Math.max(0, buffer - 0.01);
            }

        } else if (packet.isTeleport()) {
            balance -= 50;
        }
    }
}