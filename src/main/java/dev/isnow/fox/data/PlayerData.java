package dev.isnow.fox.data;

import dev.isnow.fox.Fox;
import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.impl.combat.hitbox.HitBoxA;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.processor.*;
import dev.isnow.fox.exempt.ExemptProcessor;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.manager.CheckManager;
import dev.isnow.fox.util.BlockUtil;
import dev.isnow.fox.util.LogUtil;
import dev.isnow.fox.util.PlayerUtil;
import dev.isnow.fox.util.type.ConcurrentEvictingList;
import dev.isnow.fox.util.type.Pair;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.gamestatechange.WrappedPacketOutGameStateChange;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public final class PlayerData {

    private final Player player;
    private String clientBrand;
    private int totalViolations, combatViolations, movementViolations, playerViolations;
    private long flying, lastFlying, currentTicks, lastKP;
    private final long joinTime = System.currentTimeMillis();
    private long enderpearlTime, respawnTime, setBackTicks;
    private boolean exempt, banning;
    private ClientVersion version;
    public int existedTicks;
    private LogUtil.TextFile logFile;
    @Setter private int ticks;

    private final List<Check> checks = CheckManager.loadChecks(this);
    private final Map<Check, Integer> mapchecks = CheckManager.loadChecksMap(this, checks);
    private final ConcurrentEvictingList<Pair<Location, Integer>> targetLocations = new ConcurrentEvictingList<>(40);

    private final ExemptProcessor exemptProcessor = new ExemptProcessor(this);
    private final CombatProcessor combatProcessor = new CombatProcessor(this);
    private final ActionProcessor actionProcessor = new ActionProcessor(this);
    private final ClickProcessor clickProcessor = new ClickProcessor(this);
    private final PositionProcessor positionProcessor = new PositionProcessor(this);
    private final RotationProcessor rotationProcessor = new RotationProcessor(this);
    private final VelocityProcessor velocityProcessor = new VelocityProcessor(this);
    private final ConnectionProcessor connectionProcessor = new ConnectionProcessor(this);
    private final GhostBlockProcessor ghostBlockProcessor = new GhostBlockProcessor(this);

    private HitBoxA hitboxA;

    public PlayerData(final Player player) {
        this.player = player;
        if (Config.LOGGING_ENABLED) logFile = new LogUtil.TextFile("" + player.getUniqueId(), "logs");

        if (player.hasPermission("fox.alerts")) {
            AlertManager.toggleAlerts(this);
        }

    }

    public void dragDown() {
        if (!player.isOnline()) {
            return;
        }
        final long current = System.nanoTime() / 1000_000L;
        if ((current - getSetBackTicks()) > 40) {
            double ytoAdd = player.getVelocity().getY();
            if (ytoAdd > 0) {
                return;
            }
            final Location block = player.getLocation().clone().add(0, ytoAdd, 0);
            for (int i = 0; i < 10; i++) {
                Block asyncBlock = BlockUtil.getBlockAsync(block);
                if(asyncBlock == null) continue;
                if (asyncBlock.getType().isSolid()) {
                    block.add(0, 0.1, 0);
                } else {
                    break;
                }
            }
            teleport(player, block);
        }
        setSetBackTicks(current);
        setSetBackTicks(getSetBackTicks() + 1);
    }

    public void sendDemo(Player player) {
        PacketEvents.get().getPlayerUtils().sendPacket(player, new WrappedPacketOutGameStateChange(5, 0));
    }

    public void teleport(Player player, Location location) {
        Bukkit.getScheduler().runTask(Fox.INSTANCE.getPlugin(), () -> player.teleport(location));
    }

    public ClientVersion getVersion() {
        if(version == null || !version.isResolved()) {
            version = PlayerUtil.getClientVersion(player);
        }
        return version;
    }
}
