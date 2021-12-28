package dev.isnow.fox.check.impl.player.fastbow;

import dev.isnow.fox.Fox;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.PlayerDataManager;
import dev.isnow.fox.packet.Packet;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class FastBowA implements Listener {

    HashMap<UUID, Long> timebow = new HashMap<>();
    HashMap<UUID, Double> buffer = new HashMap<>();

    @EventHandler
    public void updateUser(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getItem() == null) {
            return;
        }
        if(e.getItem().getType() != Material.BOW) {
            return;
        }
        if(e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if(!p.getInventory().contains(Material.ARROW)) {
            return;
        }
        timebow.put(p.getUniqueId(), System.currentTimeMillis());
    }

    HashMap<Player, Integer> vls = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        vls.put(event.getPlayer(), -1);
        buffer.put(event.getPlayer().getUniqueId(), (double) 0);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        vls.remove(event.getPlayer());
        buffer.remove(event.getPlayer().getUniqueId(), (double) 0);
    }

    @EventHandler
    public void performCheck(EntityShootBowEvent e) {
        if(!(e.getEntity() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getEntity();
        if(!Fox.INSTANCE.getPlugin().getConfig().getBoolean("checks.movement.speed.f.enabled")) {
            return;
        }
        PlayerData data = PlayerDataManager.getInstance().getPlayerData(p);
        if(System.currentTimeMillis() - timebow.get(p.getUniqueId()) < 20) {
            Check c = new Check(data) {
                @Override
                public void handle(Packet packet) {
                }
            };
            c.setMaxVl(Fox.INSTANCE.getPlugin().getConfig().getInt("checks.player.fastbow.a.max-violations"));
            c.setPunishCommands((ArrayList<String>) Fox.INSTANCE.getPlugin().getConfig().getStringList("checks.player.fastbow.a.punish-commands"));
            c.setCheckType(Check.CheckType.MOVEMENT);
            c.setFullName("FastBowA");
            int newint = vls.get(p);
            newint++;
            vls.put(p, newint);
            c.setVl(newint);
            c.setCustom(2);
            double buffernew = buffer.get(p.getUniqueId());
            buffernew++;
            buffer.put(p.getUniqueId(), buffernew);

            if(buffernew > 2) {
                c.fail("TIME: " + (System.currentTimeMillis() - timebow.get(p.getUniqueId())));
            }
        }
        else {
            double buffernew = buffer.get(p.getUniqueId());
            buffernew = buffernew - 0.1;
            buffer.put(p.getUniqueId(), buffernew);
        }
    }
}
