

package dev.isnow.fox.manager;

import dev.isnow.fox.Fox;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;

public final class ClientBrandListener implements PluginMessageListener, Listener {

    @Override
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] msg) {
        try {
            final PlayerData data = PlayerDataManager.getInstance().getPlayerData(player);

            if (data == null) return;
            if (msg.length == 0) return;

            final String clientBrand = new String(msg, StandardCharsets.UTF_8).length() > 0 ? new String(msg, StandardCharsets.UTF_8).substring(1) : new String(msg, StandardCharsets.UTF_8);

            data.setClientBrand(clientBrand);

            handle: {
                if (!Config.CLIENT_ENABLED) break handle;

                if (Config.CLIENT_CASE_SENSITIVE) {
                    if (Config.BLOCKED_CLIENTS.stream().noneMatch(clientBrand::contains)) {
                        break handle;
                    }
                } else {
                    if (Config.BLOCKED_CLIENTS
                            .stream().noneMatch(s -> clientBrand.toLowerCase().contains(s.toLowerCase()))) {
                        break handle;
                    }
                }

                Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () -> player.kickPlayer(Config.CLIENT_KICK_MESSAGE));
            }

            if(clientBrand.contains("lunarclient:")) {
                AlertManager.sendMessage(ColorUtil.translate(Config.CLIENT_JOIN_MESSAGE).replaceAll("%player%", data.getPlayer().getName()).replaceAll("%client%", "Lunar Client"));
            }
            else if(clientBrand.contains("Badlion-Client")) {
                AlertManager.sendMessage(ColorUtil.translate(Config.CLIENT_JOIN_MESSAGE).replaceAll("%player%", data.getPlayer().getName()).replaceAll("%client%", "Badlion"));
            }
            else if(clientBrand.contains("Tecknix-Client")) {
                AlertManager.sendMessage(ColorUtil.translate(Config.CLIENT_JOIN_MESSAGE).replaceAll("%player%", data.getPlayer().getName()).replaceAll("%client%", "Tecnix Client"));
            } else if(clientBrand.equals("vanilla")) {
                AlertManager.sendMessage(ColorUtil.translate(Config.CLIENT_JOIN_MESSAGE).replaceAll("%player%", data.getPlayer().getName()).replaceAll("%client%", "Vanilla"));
            } else if(clientBrand.equals("fabric")) {
                AlertManager.sendMessage(ColorUtil.translate(Config.CLIENT_JOIN_MESSAGE).replaceAll("%player%", data.getPlayer().getName()).replaceAll("%client%", "Fabric"));
            }
            else if(clientBrand.contains("fml,forge")){
                    AlertManager.sendMessage(ColorUtil.translate(Config.CLIENT_JOIN_MESSAGE).replaceAll("%player%", data.getPlayer().getName()).replaceAll("%client%", "Forge"));
            } else {
                AlertManager.sendMessage(ColorUtil.translate(Config.CLIENT_JOIN_MESSAGE).replaceAll("%player%", data.getPlayer().getName()).replaceAll("%client%", "UNKNOWN [" + clientBrand + "]"));
            }
        } catch (final Throwable t) {
            System.out.println("An error occurred with ClientBrandListener.");
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        addChannel(event.getPlayer());
    }

    private void addChannel(final Player player) {
        try {
            player.getClass().getMethod("addChannel", String.class).invoke(player, "MC|BRAND");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
