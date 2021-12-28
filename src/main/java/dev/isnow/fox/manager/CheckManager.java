package dev.isnow.fox.manager;

import dev.isnow.fox.check.Check;
import dev.isnow.fox.check.impl.combat.aim.*;
import dev.isnow.fox.check.impl.combat.aura.*;
import dev.isnow.fox.check.impl.combat.autoclicker.*;
import dev.isnow.fox.check.impl.combat.entity.EntityA;
import dev.isnow.fox.check.impl.combat.hitbox.HitboxA;
import dev.isnow.fox.check.impl.combat.reach.ReachX;
import dev.isnow.fox.check.impl.combat.velocity.VelocityA;
import dev.isnow.fox.check.impl.movement.bowfly.BowFlyA;
import dev.isnow.fox.check.impl.movement.fastclimb.FastClimbA;
import dev.isnow.fox.check.impl.movement.fastclimb.FastClimbB;
import dev.isnow.fox.check.impl.movement.flight.*;
import dev.isnow.fox.check.impl.movement.jesus.JesusA;
import dev.isnow.fox.check.impl.movement.jesus.JesusB;
import dev.isnow.fox.check.impl.movement.jesus.JesusC;
import dev.isnow.fox.check.impl.movement.liquidspeed.LiquidSpeedA;
import dev.isnow.fox.check.impl.movement.liquidspeed.LiquidSpeedB;
import dev.isnow.fox.check.impl.movement.liquidspeed.LiquidSpeedC;
import dev.isnow.fox.check.impl.movement.motion.*;
import dev.isnow.fox.check.impl.movement.noslow.NoSlowA;
import dev.isnow.fox.check.impl.movement.noslow.NoSlowB;
import dev.isnow.fox.check.impl.movement.noslow.NoSlowC;
import dev.isnow.fox.check.impl.movement.noslow.NoSlowD;
import dev.isnow.fox.check.impl.movement.omnisprint.OmniSprintA;
import dev.isnow.fox.check.impl.movement.speed.*;
import dev.isnow.fox.check.impl.movement.strafe.StrafeA;
import dev.isnow.fox.check.impl.player.anticactus.AntiCactusA;
import dev.isnow.fox.check.impl.player.badpackets.*;
import dev.isnow.fox.check.impl.player.breaker.BreakerA;
import dev.isnow.fox.check.impl.player.fastplace.FastPlaceA;
import dev.isnow.fox.check.impl.player.groundspoof.GroundSpoofA;
import dev.isnow.fox.check.impl.player.groundspoof.GroundSpoofB;
import dev.isnow.fox.check.impl.player.groundspoof.GroundSpoofC;
import dev.isnow.fox.check.impl.player.groundspoof.GroundSpoofD;
import dev.isnow.fox.check.impl.player.inventory.*;
import dev.isnow.fox.check.impl.player.scaffold.*;
import dev.isnow.fox.check.impl.player.timer.*;
import dev.isnow.fox.config.Config;
import dev.isnow.fox.data.PlayerData;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CheckManager {

    public static final Class<?>[] CHECKS = new Class[]{
            AimA.class,
            AimB.class,
            AimC.class,
            AimD.class,
            AimE.class,
            AimF.class,
            AimG.class,
            AimH.class,
            AimI.class,
            AimJ.class,
            AimK.class,
            AimL.class,
            AimM.class,
            AuraA.class,
            AuraB.class,
            AuraC.class,
            AuraD.class,
            AuraE.class,
            AuraF.class,
            AuraG.class,
            AuraH.class,
            AuraI.class,
            AuraJ.class,
            AuraK.class,
            AuraL.class,
            AuraM.class,
            AuraN.class,
            AuraO.class,
            AuraR.class,
            EntityA.class,
            AutoClickerF.class,
            AutoClickerA.class,
            AutoClickerB.class,
            AutoClickerC.class,
            AutoClickerD.class,
            AutoClickerE.class,
            ReachX.class,
            HitboxA.class,
            VelocityA.class,
            BowFlyA.class,
            SpeedA.class,
            SpeedB.class,
            SpeedC.class,
            SpeedD.class,
            SpeedE.class,
            SpeedG.class,
            FlightA.class,
            FlightB.class,
            FlightC.class,
            FlightD.class,
            FlightE.class,
            StrafeA.class,
            MotionA.class,
            MotionB.class,
            MotionC.class,
            MotionD.class,
            MotionE.class,
            MotionE.class,
            MotionF.class,
            MotionG.class,
            MotionH.class,
            NoSlowA.class,
            NoSlowB.class,
            NoSlowC.class,
            NoSlowD.class,
            OmniSprintA.class,
            FastClimbA.class,
            FastClimbB.class,
            JesusA.class,
            JesusB.class,
            JesusC.class,
            LiquidSpeedA.class,
            LiquidSpeedB.class,
            LiquidSpeedC.class,
            GroundSpoofA.class,
            GroundSpoofB.class,
            GroundSpoofC.class,
            GroundSpoofD.class,
            TimerA.class,
            TimerB.class,
            TimerC.class,
            TimerD.class,
            TimerE.class,
            InventoryA.class,
            InventoryB.class,
            InventoryC.class,
            InventoryD.class,
            InventoryE.class,
            InventoryF.class,
            InventoryG.class,
            FastPlaceA.class,
            ScaffoldA.class,
            ScaffoldB.class,
            ScaffoldC.class,
            ScaffoldD.class,
            ScaffoldE.class,
            ScaffoldF.class,
            ScaffoldG.class,
            ScaffoldH.class,
            ScaffoldI.class,
            ScaffoldJ.class,
            ScaffoldK.class,
            ScaffoldL.class,
            ScaffoldM.class,
            AntiCactusA.class,
            BreakerA.class,
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsD.class,
            BadPacketsE.class,
            BadPacketsF.class,
            BadPacketsG.class,
            BadPacketsH.class,
            BadPacketsI.class,
            BadPacketsJ.class,
            BadPacketsK.class,
            BadPacketsL.class,
            BadPacketsM.class,
            BadPacketsN.class,
    };

    private static final List<Constructor<?>> CONSTRUCTORSALL = new ArrayList<>();

    public static List<Check> allChecks;

    public static List<Check> loadChecks(final PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (final Constructor<?> constructor : CONSTRUCTORSALL) {
            try {
                Check check = (Check) constructor.newInstance(data);
                check.setPunishCommands((ArrayList<String>) Config.PUNISH_COMMANDS.get(constructor.getClass().getSimpleName()));
                check.setEnabled(Config.ENABLED_CHECKS.stream().anyMatch(s -> s.equals(check.getClass().getSimpleName())));
                try {
                    check.setMaxVl(Config.MAX_VIOLATIONS.get(constructor.getClass().getSimpleName()));
                } catch(NullPointerException e) {
                    check.setMaxVl(50);
                }
                checkList.add(check);
            } catch (final Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        allChecks = checkList;
        return checkList;
    }

    public static Map<Check, Integer> loadChecksMap(final PlayerData data, List<Check> checks) {
        final Map<Check, Integer> checkList = new HashMap<>();
        for (final Constructor<?> constructor : CONSTRUCTORSALL) {
            try {
                if(checks.stream().anyMatch(check -> check.getFullName().equals(constructor.getName()))) {
                    Check check = checks.stream().filter(check1 -> check1.getFullName().equals(constructor.getName())).findFirst().get();
                    checkList.put(check, 0);
                }
            } catch (final Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        return checkList;
    }
    public static void setup() {
        for (final Class<?> clazz : CHECKS) {
            if (Config.ENABLED_CHECKS.contains(clazz.getSimpleName())) {
                try {
                    CONSTRUCTORSALL.add(clazz.getConstructor(PlayerData.class));
                    Bukkit.getLogger().info(clazz.getSimpleName() + " is enabled!");
                } catch (final NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            } else {
                try {
                    CONSTRUCTORSALL.add(clazz.getConstructor(PlayerData.class));
                    Bukkit.getLogger().info(clazz.getSimpleName() + " is disabled!");
                } catch (final NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}

