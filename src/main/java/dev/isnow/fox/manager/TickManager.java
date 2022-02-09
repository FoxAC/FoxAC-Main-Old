

package dev.isnow.fox.manager;

import dev.isnow.fox.Fox;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.type.Pair;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

public final class TickManager implements Runnable {

    @Getter
    private int ticks;

    @Getter
    private int a = 0;

    @Getter
    private float time = System.currentTimeMillis();
    
    private static BukkitTask task;

    public void start() {
        assert task == null : "TickProcessor has already been started!";

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Fox.INSTANCE.getPlugin(), () -> {
            for (final Player p : Bukkit.getOnlinePlayers()) {
                AFKManager.INSTANCE.updatePlayer(p);
            }
        }, 0L, 600L);
        final long[] lastTick = { System.currentTimeMillis() };
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Fox.INSTANCE.getPlugin(), () -> {
            a = (int)(System.currentTimeMillis() - lastTick[0]);
            lastTick[0] = System.currentTimeMillis();
            if(a > 102) {
                time = System.currentTimeMillis();
            }
        }, 1L, 1L);
        task = Bukkit.getScheduler().runTaskTimer(Fox.INSTANCE.getPlugin(), this, 0L, 1L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Fox.INSTANCE.getPlugin(), () -> {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(PlayerDataManager.getInstance().getPlayerData(p) != null) {
                    PlayerDataManager.getInstance().getPlayerData(p).getConnectionProcessor().sendTransaction();
                }
            }
        }, 1L, 0L);
    }

    public void stop() {
        if (task == null) return;

        task.cancel();
        task = null;
    }


    @Override
    public void run() {
        ++ticks;
        for (final PlayerData data : PlayerDataManager.getInstance().getAllData()) {
            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();

            if (target != null && lastTarget != null) {
                if (target != lastTarget) data.getTargetLocations().clear();

                final Location location = target.getLocation();
                data.getTargetLocations().add(new Pair<>(location, ticks));
            }
        }
    }
}
