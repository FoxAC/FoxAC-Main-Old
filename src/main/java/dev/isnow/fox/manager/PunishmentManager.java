package dev.isnow.fox.manager;

import dev.isnow.fox.Fox;
import dev.isnow.fox.api.APIManager;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public final class PunishmentManager {
    public static void punish(final Check check, final PlayerData data) {
        APIManager.callPunishEvent(check);
        check.setPunishCommands((ArrayList<String>) Config.PUNISH_COMMANDS.get(check.getClass().getSimpleName()));
            if(!Config.GLOBALCMD) {
                if (check.getPunishCommands() != null) {
                    for(String s : check.getPunishCommands()) {
                        if(!s.equals("")) {
                            s = ChatColor.translateAlternateColorCodes('&', s);
                            s = s.replaceAll("%player%", data.getPlayer().getName())
                                    .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                    .replaceAll("%check%", check.getCheckInfo().name())
                                    .replaceAll("%vl%", String.valueOf(check.getVl()))
                                    .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                    .replaceAll("%type%", check.getCheckInfo().type());
                            String finalS = s;
                            Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                        }
                    }
                }
                if(Config.BANTIMER) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (String s : Config.TIMER_COMMANDS) {
                                if (!s.equals("")) {
                                    s = ChatColor.translateAlternateColorCodes('&', s);
                                    s = s.replaceAll("%player%", data.getPlayer().getName())
                                            .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                            .replaceAll("%check%", check.getCheckInfo().name())
                                            .replaceAll("%vl%", String.valueOf(check.getVl()))
                                            .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                            .replaceAll("%type%", check.getCheckInfo().type());
                                    String finalS = s;
                                    Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                                }
                            }
                        }
                    }.runTaskLater(Fox.INSTANCE.getPlugin(), Config.BANTIMERTIME * 20L);
                }
            } else {
                for (String s : Config.GLOBAL_COMMANDS) {
                    if (!s.equals("")) {
                        s = ChatColor.translateAlternateColorCodes('&', s);
                        s = s.replaceAll("%player%", data.getPlayer().getName())
                                .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                .replaceAll("%check%", check.getCheckInfo().name())
                                .replaceAll("%vl%", String.valueOf(check.getVl()))
                                .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                .replaceAll("%type%", check.getCheckInfo().type());
                        String finalS = s;
                        Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                    }
                }
                if (Config.BANTIMER) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (String s : Config.TIMER_COMMANDS) {
                                if (!s.equals("")) {
                                    s = ChatColor.translateAlternateColorCodes('&', s);
                                    s = s.replaceAll("%player%", data.getPlayer().getName())
                                            .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                            .replaceAll("%check%", check.getCheckInfo().name())
                                            .replaceAll("%vl%", String.valueOf(check.getVl()))
                                            .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                            .replaceAll("%type%", check.getCheckInfo().type());
                                    String finalS = s;
                                    Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                                }
                            }
                        }
                    }.runTaskLater(Fox.INSTANCE.getPlugin(), Config.BANTIMERTIME * 20L);
                }
            }

            if (Config.LOGGING_ENABLED) {
//                final String log = String.format(,
//                        data.getPlayer().getName(), check.getCheckInfo().name(),
//                        data.getConnectionProcessor().getTransactionPing(), data.getExemptProcessor().isExempt(ExemptType.LAGGING),
//                        ServerUtil.getTPS(), data.getPositionProcessor().getDeltaXZ(), data.getPositionProcessor().getDeltaY());
//
//                LogUtil.logToFile(data.getLogFile(), log);
            }
        }
}