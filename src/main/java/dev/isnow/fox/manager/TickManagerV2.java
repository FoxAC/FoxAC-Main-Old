package dev.isnow.fox.manager;


import dev.isnow.fox.Fox;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.impl.combat.hitbox.HitBoxA;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.HookedListWrapper;
import io.github.retrooper.packetevents.utils.nms.NMSUtils;
import io.github.retrooper.packetevents.utils.reflection.Reflection;
import org.bukkit.Bukkit;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TickManagerV2 implements Initable{

    boolean hasTicked = true;
    boolean messageSent = false;

    private static void tickRelMove() {
        for (PlayerData player : PlayerDataManager.getInstance().getAllData()) {
            Optional<Check> hitboxA = player.getChecks().stream().filter(check -> check.getFullName().equals("HitBoxA")).findFirst();

            if(hitboxA.isPresent() && hitboxA.get() instanceof HitBoxA) {
                HitBoxA hitbox = (HitBoxA) hitboxA.get();

                hitbox.tickEndEvent();
            }
        }
    }

    @Override
    public void start() {
        try {
            Object connection = NMSUtils.getMinecraftServerConnection();

            Field connectionsList = Reflection.getField(connection.getClass(), List.class, 1);
            List<Object> endOfTickObject = (List<Object>) connectionsList.get(connection);

            // Use a list wrapper to check when the size method is called
            // Unsure why synchronized is needed because the object itself gets synchronized
            // but whatever.  At least plugins can't break it, I guess.
            //
            // Pledge injects into another list, so we should be safe injecting into this one
            List<?> wrapper = Collections.synchronizedList(new HookedListWrapper<Object>(endOfTickObject) {
                @Override
                public void onIterator() {
                    hasTicked = true;
                    tickRelMove();
                }
            });

            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            unsafe.putObject(connection, unsafe.objectFieldOffset(connectionsList), wrapper);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskTimer(Fox.INSTANCE.getPlugin(), () -> {
            if (!hasTicked) {
                if(!messageSent) {
                    Bukkit.getConsoleSender().sendMessage("§cFOX §8>> §fCouldn't hook into TickEndEvent! Using BukkitRunnables (Reach check wont be able to detect 3.005!)....");
                    messageSent = true;
                }
                tickRelMove();
            }

            hasTicked = false;
        }, 2, 1);
    }
}

 interface Initable {
    void start();
}