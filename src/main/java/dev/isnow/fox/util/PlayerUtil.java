

package dev.isnow.fox.util;

import dev.isnow.fox.data.PlayerData;
import dev.isnow.fox.util.vpn.VPNResponse;
import dev.isnow.fox.util.vpn.json.JSONException;
import dev.isnow.fox.util.vpn.json.JSONObject;
import dev.isnow.fox.util.vpn.json.JsonReader;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@UtilityClass
public class PlayerUtil {

    public ClientVersion getClientVersion(final Player player) {
        return PacketEvents.get().getPlayerUtils().getClientVersion(player);
    }

    public int getPing(final Player player) {
        return PacketEvents.get().getPlayerUtils().getPing(player.getUniqueId());
    }

    public boolean isOnBoat(PlayerData user) {
        double offset = user.getPositionProcessor().getY() % 0.015625;
        if ((user.getPositionProcessor().isOnGround() && offset > 0 && offset < 0.009)) {
            return getEntitiesWithinRadius(user.getPlayer().getLocation(), 2).stream()
                    .anyMatch(entity -> entity.getType() == EntityType.BOAT);
        }

        return false;
    }

    public static VPNResponse getVPNResponse(String ip) throws JSONException, IOException {
        JSONObject result = JsonReader.readJsonFromUrl(String
                .format("https://funkemunky.cc/vpn?ip=%s&license=%s",
                        ip, "none"));

        return VPNResponse.fromJson(result);
    }
    public VPNResponse isUsingVPN(final Player player) {
        try {
            return getVPNResponse(player.getAddress().getAddress().getHostAddress());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean isHoldingSword(final Player player) {
        return player.getItemInHand().getType().toString().toLowerCase().contains("sword");
    }

    public boolean is1_7(final Player player) {
        return !is1_8(player);
    }

    public boolean is1_8(final Player player) {
        return PacketEvents.get().getPlayerUtils().getClientVersion(player).getProtocolVersion() >= 47;
    }

    public float getBaseSpeed(final Player player, final float base) {
        return base + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public double getBaseSpeed(final Player player) {
        return 0.3615 + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public double getBaseGroundSpeed(final Player player) {
        return 0.289 + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    /**
     * Bukkit's getNearbyEntities method looks for all entities in all chunks
     * This is a lighter method and can also be used Asynchronously since we won't load any chunks
     *
     * @param location The location to scan for nearby entities
     * @param radius   The radius to expand
     * @return The entities within that radius
     * @author Nik
     */
    public List<Entity> getEntitiesWithinRadius(final Location location, final double radius) {
        final double expander = 16.0D;

        final double x = location.getX();
        final double z = location.getZ();

        final int minX = (int) Math.floor((x - radius) / expander);
        final int maxX = (int) Math.floor((x + radius) / expander);

        final int minZ = (int) Math.floor((z - radius) / expander);
        final int maxZ = (int) Math.floor((z + radius) / expander);

        final World world = location.getWorld();

        final List<Entity> entities = new LinkedList<>();

        try {
            for (int xVal = minX; xVal <= maxX; xVal++) {

                for (int zVal = minZ; zVal <= maxZ; zVal++) {

                    if (!world.isChunkLoaded(xVal, zVal)) continue;
                    if(world.getChunkAt(xVal, zVal) == null) {
                        continue;
                    }
                    for (final Entity entity : world.getChunkAt(xVal, zVal).getEntities()) {

                        if (entity == null) continue;


                        if (entity.getLocation().distanceSquared(location) > radius * radius) continue;

                        entities.add(entity);
                    }
                }
            }
        } catch(NoSuchElementException ignored) {

        }

        return entities;
    }

    public int getPotionLevel(final Player player, final PotionEffectType effect) {
        final int effectId = effect.getId();

        if (!player.hasPotionEffect(effect)) return 0;

        return player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effectId).map(PotionEffect::getAmplifier).findAny().orElse(0) + 1;
    }

}
