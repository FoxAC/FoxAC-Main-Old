package dev.isnow.fox.data;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.processor.*;
import dev.isnow.fox.exempt.ExemptProcessor;
import dev.isnow.fox.manager.AlertManager;
import dev.isnow.fox.manager.CheckManager;
import dev.isnow.fox.util.LogUtil;
import dev.isnow.fox.util.type.ConcurrentEvictingList;
import dev.isnow.fox.util.type.EntityHelper;
import dev.isnow.fox.util.type.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public final class PlayerData {

    private final Player player;
    private String clientBrand;
    private int totalViolations, combatViolations, movementViolations, playerViolations, botViolations;
    private long flying, lastFlying, currentTicks, lastKP;
    private final long joinTime = System.currentTimeMillis();
    private long enderpearlTime, respawnTime;
    private boolean exempt, banning;
    private EntityHelper entityHelper;
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
    private final BotProcessor botProcessor = new BotProcessor();
    public PlayerData(final Player player) {
        this.player = player;
        if (Config.LOGGING_ENABLED) logFile = new LogUtil.TextFile("" + player.getUniqueId(), "\\logs");

        if (player.hasPermission("fox.alerts")) {
            AlertManager.toggleAlerts(this);
        }

        entityHelper = new EntityHelper();
    }
}
