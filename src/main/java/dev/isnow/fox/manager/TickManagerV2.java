package dev.isnow.fox.manager;


import dev.isnow.fox.Fox;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.HookedListWrapper;
import io.github.retrooper.packetevents.utils.nms.NMSUtils;
import io.github.retrooper.packetevents.utils.reflection.Reflection;
import org.bukkit.Bukkit;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class TickManagerV2 implements Initable{

    boolean hasTicked = true;
    boolean messageSent = false;

    private static void tickRelMove() {
        for (PlayerData player : PlayerDataManager.getInstance().getAllData()) {
            if(player.getHitboxA() != null) {
                player.getHitboxA().tickEndEvent();
            }
        }
    }

    @Override
    public void start() {
        try {
            Object connection = NMSUtils.getMinecraftServerConnection();

            Field connectionsList = Reflection.getField(connection.getClass(), List.class, 1);
            List<Object> endOfTickObject = (List<Object>) connectionsList.get(connection);

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