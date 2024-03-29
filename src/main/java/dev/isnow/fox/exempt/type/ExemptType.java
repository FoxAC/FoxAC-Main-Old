

package dev.isnow.fox.exempt.type;

import dev.isnow.fox.Fox;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.manager.AFKManager;
import dev.isnow.fox.util.PlayerUtil;
import dev.isnow.fox.util.ServerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;

import java.util.function.Function;

@Getter
public enum ExemptType {

    CHUNK(data -> !data.getPlayer().getWorld().isChunkLoaded(
            NumberConversions.floor(data.getPositionProcessor().getX()) >> 4,
            NumberConversions.floor(data.getPositionProcessor().getZ()) >> 4)
    ),

    TPS(data -> ServerUtil.getTPS() < 17.0D),

    NEAR_WALL(data -> data.getPositionProcessor().isNearWall()),

    TELEPORT(data -> data.getPositionProcessor().isTeleported()),

    TELEPORT_DELAY_2TICK(data -> data.getPositionProcessor().getTeleportTicks() < 3),

    TELEPORT_DELAY(data -> data.getPositionProcessor().getTeleportTicks() < 5),

    UPWARDS_VEL(e -> e.getVelocityProcessor().getVelocityY() > 0.0 && e.getVelocityProcessor().isTakingVelocity()),

    VELOCITY(data -> data.getVelocityProcessor().isTakingVelocity()),

    JUMP(data -> {
        final boolean onGround = data.getPositionProcessor().isOnGround();
        final boolean lastOnGround = data.getPositionProcessor().isLastOnGround();

        final double deltaY = data.getPositionProcessor().getDeltaY();
        final double lastY = data.getPositionProcessor().getLastY();

        final boolean deltaModulo = deltaY % 0.015625 == 0.0;
        final boolean lastGround = lastY % 0.015625 == 0.0;

        final boolean step = deltaModulo && lastGround;

        final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
        final double expectedJumpMotion = 0.42F + modifierJump;

        return Math.abs(expectedJumpMotion - deltaY) < 1E-5 && !onGround && lastOnGround && !step;
    }),

    PEARL(data -> data.getEnderpearlTime() != 0 && System.currentTimeMillis() - data.getEnderpearlTime() < 5000L),


    AFK(data -> AFKManager.INSTANCE.isAFK(data.getPlayer())),

    RESPAWN(data -> data.getRespawnTime() != 0 && System.currentTimeMillis() - data.getRespawnTime() < 5000L),

    NEARCACTUS(data -> data.getPositionProcessor().isNearCactus()),

    VELOCITY_ON_TICK(data -> data.getVelocityProcessor().getTicksSinceVelocity() < 2),

    SLIME(data -> data.getPositionProcessor().getSinceSlimeTicks() < 20),

    SLIME_ON_TICK(data -> data.getPositionProcessor().getSinceSlimeTicks() < 2),

    DEAD(data -> data.getPlayer().isDead()),

    NEARHORSE(data -> data.getPositionProcessor().getNearbyEntities().stream().anyMatch(entity -> entity.getType() == EntityType.HORSE)),

    FIRE(data -> data.getPlayer().getFireTicks() > 0),

    NEARSLIME(data -> data.getPositionProcessor().getBlocks().stream().anyMatch(block -> block.getType().toString().contains("SLIME"))),

    NEARANVIL(data -> data.getPositionProcessor().getBlocks().stream().anyMatch(block -> block.getType().toString().contains("ANVIL"))),

    DIGGING(data -> Fox.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastDiggingTick() < 10),

    BLOCK_BREAK(data -> Fox.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastBreakTick() < 10),

    PLACING(data -> Fox.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastPlaceTick() < 10),

    BUKKIT_PLACING(data -> Fox.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastBukkitPlaceTick() < 10),

    LONG_BUKKIT_PLACING(data -> Fox.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastBukkitPlaceTick() < 25),

    BOAT(data -> data.getPositionProcessor().isNearVehicle()),

    VEHICLE(data -> data.getPositionProcessor().getSinceVehicleTicks() < 20),

    VEHICLE_NO_DELAY(data -> data.getPositionProcessor().isInVehicle()),

    VEHICLE_DELAY_2TICK(data -> data.getPositionProcessor().getSinceVehicleTicks() < 3) ,

    LIQUID(data -> data.getPositionProcessor().getSinceLiquidTicks() < 4),

    NEARSTAIRS(data -> data.getPositionProcessor().isNearStair()),

    NEARSLABS(data -> data.getPositionProcessor().getBlocks().stream().noneMatch(block -> block.getType().toString().contains("SLAB"))),

    NEARICE(data -> data.getPositionProcessor().getBlocks().stream().noneMatch(block -> block.getType().toString().contains("ICE"))),

    DROP(data -> data.getActionProcessor().getLastDropTick() > 10),

    ONBED(data -> data.getPositionProcessor().getBlocksBelow().stream().noneMatch(block -> block.getType().toString().contains("BED"))),

    GETTINGCOMBOED(data -> data.getCombatProcessor().getHitTicks() <= 10),

    UNDERBLOCK(data -> data.getPositionProcessor().isBlockNearHead()),

    WASUNDERBLOCK(data -> data.getPositionProcessor().getSinceBlockNearHeadTicks() <= 10),

    PISTON(data -> data.getPositionProcessor().isNearPiston()),

    VOID(data -> data.getPositionProcessor().getY() < 4),

    COMBAT(data -> data.getCombatProcessor().getHitTicks() < 5),

    FLYING(data -> data.getPositionProcessor().getSinceFlyingTicks() < 40),

    AUTOCLICKER(data -> data.getExemptProcessor().isExempt(ExemptType.PLACING, ExemptType.DIGGING, ExemptType.BLOCK_BREAK)),

    WEB(data -> data.getPositionProcessor().getSinceWebTicks() < 10),

    JOINED(data -> System.currentTimeMillis() - data.getJoinTime() < 5000L),

    LONG_JOINED(data -> System.currentTimeMillis() - data.getJoinTime() < 15000L),

    LAGGING(data -> {
        final long delta = data.getFlying() - data.getLastFlying();

        return delta > 100 || delta < 2;
    }),

    LAGGINGHARD(data -> {
        final int ping = PacketEvents.get().getPlayerUtils().getPing(data.getPlayer());

        return ping >= 200;
    }),

    CREATIVE(data -> data.getPlayer().getGameMode() == GameMode.CREATIVE),

    GHOST_BLOCK(data -> data.getGhostBlockProcessor().isOnGhostBlock()),

    INWEB(data -> data.getPositionProcessor().isInWeb()),

    CINEMATIC(data -> data.getRotationProcessor().isCinematic()),

    CINEMATIC_TIME(data -> System.currentTimeMillis() - data.getRotationProcessor().getCinematicTime() < 5000L),

    CLIMBABLE(data -> data.getPositionProcessor().getSinceClimbableTicks() < 10),

    ICE(data -> data.getPositionProcessor().getSinceIceTicks() < 10);

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }
}
