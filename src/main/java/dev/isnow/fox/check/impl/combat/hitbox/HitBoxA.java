package dev.isnow.fox.check.impl.combat.hitbox;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.api.CheckInfo;
import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.exempt.type.ExemptType;
import dev.isnow.fox.packet.Packet;
import dev.isnow.fox.util.MathUtil;
import dev.isnow.fox.util.reach.reach.PlayerReachEntity;
import dev.isnow.fox.util.reach.reach.ReachUtils;
import dev.isnow.fox.util.reach.reach.SimpleCollisionBox;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity;
import io.github.retrooper.packetevents.packetwrappers.play.out.entitydestroy.WrappedPacketOutEntityDestroy;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport;
import io.github.retrooper.packetevents.packetwrappers.play.out.namedentityspawn.WrappedPacketOutNamedEntitySpawn;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import io.github.retrooper.packetevents.utils.vector.Vector3d;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@CheckInfo(name = "HitBox", type = "A", description = "Modified HitBox")
public class HitBoxA extends Check {

    public final ConcurrentHashMap<Integer, PlayerReachEntity> entityMap = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Integer> playerAttackQueue = new ConcurrentLinkedQueue<>();

    private boolean hasSentPreWavePacket = false;
    private boolean lastPosition, position;

    public HitBoxA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isSpawnEntity()) {
            WrappedPacketOutNamedEntitySpawn spawn = new WrappedPacketOutNamedEntitySpawn(packet.getRawPacket());
            Entity entity = spawn.getEntity();

            if (entity != null && entity.getType() == EntityType.PLAYER) {
                handleSpawnPlayer(spawn.getEntityId(), spawn.getPosition());
            }
        } else if (packet.isRelEntityMove()) {
            WrappedPacketOutEntity.WrappedPacketOutRelEntityMove move = new WrappedPacketOutEntity.WrappedPacketOutRelEntityMove(packet.getRawPacket());

            PlayerReachEntity reachEntity = entityMap.get(move.getEntityId());
            if (reachEntity != null) {
                // We can't hang two relative moves on one transaction
                if (reachEntity.lastTransactionHung == data.getConnectionProcessor().getLastTransactionSent().get())
                    data.getConnectionProcessor().sendTransaction();
                reachEntity.lastTransactionHung = data.getConnectionProcessor().getLastTransactionSent().get();
                handleMoveEntity(move.getEntityId(), move.getDeltaX(), move.getDeltaY(), move.getDeltaZ(), true);
            }
        } else if (packet.isEntityTeleport()) {
            WrappedPacketOutEntityTeleport teleport = new WrappedPacketOutEntityTeleport(packet.getRawPacket());

            PlayerReachEntity reachEntity = entityMap.get(teleport.getEntityId());
            if (reachEntity != null) {
                if (reachEntity.lastTransactionHung == data.getConnectionProcessor().getLastTransactionSent().get())
                    data.getConnectionProcessor().sendTransaction();
                reachEntity.lastTransactionHung = data.getConnectionProcessor().getLastTransactionSent().get();

                Vector3d pos = teleport.getPosition();
                handleMoveEntity(teleport.getEntityId(), pos.getX(), pos.getY(), pos.getZ(), false);
            }
        } else if (packet.isUseEntity()) {
            WrappedPacketInUseEntity action = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (data.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            //if (data.getPositionProcessor().isInVehicle()) return;

            if (action.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                checkReach(action.getEntityId());
            }

        } else if (packet.isFlyingType()) {
            if (!data.getExemptProcessor().isExempt(ExemptType.TELEPORT)) {
                position = packet.getPacketId() == PacketType.Play.Client.POSITION || packet.getPacketId() == PacketType.Play.Client.POSITION_LOOK;
                tickFlying();
            }
            lastPosition = position;
        } else if (packet.isEntityRem()) {
            WrappedPacketOutEntityDestroy destroy = new WrappedPacketOutEntityDestroy(packet.getRawPacket());
            int lastTransactionSent = data.getConnectionProcessor().getLastTransactionSent().get();
            int[] destroyEntityIds = destroy.getEntityIds();
            for (int integer : destroyEntityIds) {
                PlayerReachEntity entity = entityMap.get(integer);
                if (entity == null) continue;
                entity.setDestroyed(lastTransactionSent + 1);
            }
        } else if (packet.isTransaction()) {
            synchronized (entityMap) {
                List<Integer> entitiesToRemove = null;
                for (Map.Entry<Integer, PlayerReachEntity> entry : entityMap.entrySet()) {
                    PlayerReachEntity entity = entry.getValue();
                    if (entity == null) continue;
                    if (entity.removeTrans > data.getConnectionProcessor().getLastTransactionSent().get()) continue;
                    int entityID = entry.getKey();

                    if (entitiesToRemove == null) entitiesToRemove = new ArrayList<>();
                    entitiesToRemove.add(entityID);
                }
                if (entitiesToRemove != null) {
                    for (int entityID : entitiesToRemove) {
                        entityMap.remove(entityID);
                        //debug("Entity destroyed");
                    }
                }
            }
        }
    }

    public void tickFlying() {
        double maxReach = 3.0075;
        Integer attackQueue = playerAttackQueue.poll();
        while (attackQueue != null) {
            final PlayerReachEntity reachEntity = entityMap.get(attackQueue);
            final SimpleCollisionBox targetBox = reachEntity.getPossibleCollisionBoxes();

            targetBox.expand(0.1025f);

            if (!position) {
                targetBox.expand(0.05);
            }
            if (!lastPosition) {
                targetBox.expand(0.05);
            }

            final Location from = new Location(null, data.getPositionProcessor().getLastX(), data.getPositionProcessor().getLastY(), data.getPositionProcessor().getLastZ(), data.getRotationProcessor().getYaw(), data.getRotationProcessor().getPitch());

            double minDistance = Double.MAX_VALUE;

            List<Vector> possibleLookDirs = new ArrayList<>(Arrays.asList(
                    ReachUtils.getLook(data.getRotationProcessor().getLastYaw(), data.getRotationProcessor().getLastPitch()),
                    ReachUtils.getLook(data.getRotationProcessor().getYaw(), data.getRotationProcessor().getPitch())
            ));

            if (PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_9)) {
                possibleLookDirs.add(ReachUtils.getLook(data.getRotationProcessor().getLastYaw(), data.getRotationProcessor().getLastPitch()));
            }

            for (Vector lookVec : possibleLookDirs) {
                for (double eye : Arrays.asList(1.54, 1.62) ) {
                    final Vector eyePos = new Vector(from.getX(), from.getY() + eye, from.getZ());
                    final Vector endReachPos = eyePos.clone().add(new Vector(lookVec.getX() * 6, lookVec.getY() * 6, lookVec.getZ() * 6));

                    final Vector intercept = ReachUtils.calculateIntercept(targetBox, eyePos, endReachPos);

                    if (ReachUtils.isVecInside(targetBox, eyePos)) {
                        minDistance = 0;
                        break;
                    }

                    if (intercept != null) {
                        minDistance = Math.min(eyePos.distance(intercept), minDistance);
                    }
                }
            }

            if (minDistance == Double.MAX_VALUE) {
                fail("");
            } else if (minDistance > maxReach) {
                fail("Reach: " + MathUtil.preciseRound(minDistance, 2));
            }

            attackQueue = playerAttackQueue.poll();
        }

        for (PlayerReachEntity entity : entityMap.values()) {
            entity.onMovement();
        }
    }

    public void checkReach(int entityID) {
        if (entityMap.containsKey(entityID))
            playerAttackQueue.add(entityID);
    }

    private void handleSpawnPlayer(int playerID, Vector3d spawnPosition) {
        entityMap.put(playerID, new PlayerReachEntity(spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ(), data));
    }

    private void handleMoveEntity(int entityId, double deltaX, double deltaY, double deltaZ, boolean isRelative) {
        PlayerReachEntity reachEntity = entityMap.get(entityId);

        if (reachEntity != null) {
            // Only send one transaction before each wave, without flushing
            if (!hasSentPreWavePacket) data.getConnectionProcessor().sendTransaction();
            hasSentPreWavePacket = true; // Also functions to mark we need a post wave transaction

            // Update the tracked server's entity position
            if (isRelative)
                reachEntity.serverPos = reachEntity.serverPos.add(new Vector3d(deltaX, deltaY, deltaZ));
            else
                reachEntity.serverPos = new Vector3d(deltaX, deltaY, deltaZ);

            int lastTrans = data.getConnectionProcessor().getLastTransactionSent().get();
            Vector3d newPos = reachEntity.serverPos;

            data.getConnectionProcessor().addRealTimeTask(lastTrans, () -> reachEntity.onFirstTransaction(newPos.getX(), newPos.getY(), newPos.getZ(), data));
            data.getConnectionProcessor().addRealTimeTask(lastTrans + 1, reachEntity::onSecondTransaction);
        }
    }
}