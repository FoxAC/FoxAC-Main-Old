

package dev.isnow.fox.manager;

import dev.isnow.fox.Fox;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.ColorUtil;
import dev.isnow.fox.util.LogUtil;
import dev.isnow.fox.util.ServerUtil;
import dev.isnow.fox.util.discord.DiscordWebhook;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
public final class AlertManager {

    private static final Set<PlayerData> alerts = new HashSet<>();
    private static final Set<PlayerData> verbose = new HashSet<>();
    public static final Set<PlayerData> packetlog = new HashSet<>();

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, MMMMM d, yyyy hh:mm aaa");
    private static long lastAlert;

    public static ToggleAlertType toggleAlerts(final PlayerData data) {
        if (alerts.contains(data)) {
            alerts.remove(data);
            return ToggleAlertType.REMOVE;
        } else {
            alerts.add(data);
            return ToggleAlertType.ADD;
        }
    }

    public static ToggleAlertType togglePacketlog(final PlayerData data) {
        if (packetlog.contains(data)) {
            packetlog.remove(data);
            return ToggleAlertType.REMOVE;
        } else {
            packetlog.add(data);
            return ToggleAlertType.ADD;
        }
    }

    public static ToggleAlertType toggleVerbose(final PlayerData data) {
        if (verbose.contains(data)) {
            verbose.remove(data);
            return ToggleAlertType.REMOVE;
        } else {
            verbose.add(data);
            return ToggleAlertType.ADD;
        }
    }

    public static void handleAlert(final Check check, final PlayerData data, final String colour, String info) {

        if (Config.LOGGING_ENABLED) {
            final String log = Config.LOG_FORMAT.replaceAll("%player%", data.getPlayer().getName())
                    .replaceAll("%check%", check.getCheckInfo().name())
                    .replaceAll("%dev%", check.getCheckInfo().experimental() ? "*" : "")
                    .replaceAll("%vl%", Integer.toString(check.getVl()))
                    .replaceAll("%type%", check.getCheckInfo().type())
                    .replaceAll("%date%", new Date().toString())
                    .replaceAll("%bar%", ColorUtil.translate(check.getBar()))
                    .replaceAll("%tps%", String.valueOf(Math.min(ServerUtil.getTPS(), 20.0)))
                    .replaceAll("%info%", info)
                    .replaceAll("%tping%", String.valueOf(data.getConnectionProcessor().getTransactionPing()));

            LogUtil.logToFile(data.getLogFile(), log);
        }

        final TextComponent alertMessage = new TextComponent(ColorUtil.translate(Config.ALERT_FORMAT)
                .replaceAll("%player%", data.getPlayer().getName())
                .replaceAll("%check%", check.getCheckInfo().name())
                .replaceAll("%dev%", check.getCheckInfo().experimental() ? "*" : "")
                .replaceAll("%vl%", Integer.toString(check.getVl()))
                .replaceAll("%colour%", colour)
                .replaceAll("%bar%", ColorUtil.translate(check.getBar()))
                .replaceAll("%type%", check.getCheckInfo().type())
                .replaceAll("%maxvl%", String.valueOf(check.getMaxVl())));

        alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Config.CLICKCOMMAND.replaceAll("%player%", data.getPlayer().getName())));
        alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.translate(
                "&7➠ " + check.getCheckInfo().description() +
                        "\n &7Info: &c" + info +
                        "\n &7(Ping: " + PacketEvents.get().getPlayerUtils().getPing(data.getPlayer()) + ") &8(Click to teleport.)")).create()));

        alerts.forEach(player -> player.getPlayer().spigot().sendMessage(alertMessage));

        if(Config.WEBHOOK) {
            if(System.currentTimeMillis() - lastAlert > 1500) {
                DiscordWebhook webhook = new DiscordWebhook(Config.ALERT_URL);
                DiscordWebhook banWebhook = new DiscordWebhook(Config.BAN_URL);

                webhook.setUsername(Config.DISCORDNAME);
                webhook.addEmbed(new DiscordWebhook.EmbedObject().setAuthor(Config.DISCORDNAME, null, null)
                        .setDescription("```md\\n" +
                                "" + data.getPlayer().getName() + " failed [" + check.getCheckInfo().name() + "] (" + check.getCheckInfo().type() + ") (VL:" + check.getVl() + ")```" +

                                "```md\\n" +
                                "                -Information-\\n" +
                                "\\n" +
                                "* Server: " + Bukkit.getServer().getName() + "\\n" +
                                "* Ping: " + data.getConnectionProcessor().getTransactionPing() + "\\n" +
                                "* TPS: " + ServerUtil.getTPS() + "\\n" +
                                "* Client: " + data.getClientBrand().replaceAll("vanilla", "Vanilla").replaceAll("Tecknix-Client", "Tecknix").replaceAll("fml, forge", "Forge") + "\\n" +
                                "* Past VL: " + data.getTotalViolations() +"```\\n" +

                                "```md\\n" +
                                "            -Check Information-\\n" +
                                "\\n" +
                                "* Description: " + check.getCheckInfo().description() + "\\n" +
                                "* Info: " + (info.length() >= 2000 ? info.substring(0, 2000) : info) + "\\n" +
                                "```")
                        .setThumbnail("https://minotar.net/avatar/" + data.getPlayer().getName() + "/64")
                        .setFooter(simpleDateFormat.format(new Date(System.currentTimeMillis())), ""));

                try {
                    webhook.execute();
                } catch (IOException exception) {
                    exception.printStackTrace();

                }
                lastAlert = System.currentTimeMillis();
            }
        }
    }

    public static void handleVerbose(final Check check, final PlayerData data) {
        final TextComponent alertMessage = new TextComponent(ColorUtil.translate(Config.VERBOSE_FORMAT)
                .replaceAll("%player%", data.getPlayer().getName())
                .replaceAll("%check%", check.getCheckInfo().name())
                .replaceAll("%dev%", check.getCheckInfo().experimental() ? "*" : "")
                .replaceAll("%type%", check.getCheckInfo().type()));

        alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, Config.CLICKCOMMAND.replaceAll("%player%", data.getPlayer().getName())));
        alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.translate(
                "&7➠ " + check.getCheckInfo().description() +
                        "\n &7(Ping: " + PacketEvents.get().getPlayerUtils().getPing(data.getPlayer()) + ") &8(Click to teleport.)")).create()));

        verbose.forEach(player -> player.getPlayer().spigot().sendMessage(alertMessage));
    }

    public static void handleAlertLag(final Check check, final PlayerData data) {
        final float calc = (System.currentTimeMillis() - Fox.INSTANCE.getTickManager().getTime()) / 1000;

        if(check.getCheckInfo() != null && calc < 5) {
            alerts.forEach(player -> player.getPlayer().sendMessage(ColorUtil.translate(Config.PREFIX + data.getPlayer().getName() + " would flag for " + check.getFullName()) + ", but server skipped " + Fox.INSTANCE.getTickManager().getA() + "MS/" + (Fox.INSTANCE.getTickManager().getA() / 50) + " Ticks in the last " + calc + " seconds."));
        }
    }

    public static void sendMessage(final String message) {
        alerts.forEach(player -> player.getPlayer().sendMessage(ColorUtil.translate(message)));
    }

    public static void sendVPNMessage(final String message) {
        alerts.forEach(player -> player.getPlayer().sendMessage(ColorUtil.translate(Config.PREFIX + message)));
    }

    public static void sendAntiExploitAlert(final String info, String check) {
        alerts.forEach(player -> player.getPlayer().sendMessage(ColorUtil.translate(Config.ANTICRASHALERT.replaceAll("%player%", player.getPlayer().getName()).replaceAll("%check%", check).replaceAll("%info%", info))));
    }

    public enum ToggleAlertType {
        ADD, REMOVE
    }
}
