package dev.gwgs.realisticbloodphysics.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class Tools {

    public static Vec3 floorVec3(Vec3 vec3) {
        return new Vec3(Math.floor(vec3.x),Math.floor(vec3.y),Math.floor(vec3.z));
    }

    public static BlockPos vec3ToBlockPos(Vec3 vec3) {
        return new BlockPos((int) vec3.x, (int) vec3.y,(int) vec3.z);
    }

    public static double random(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }

    public static double getVolumeFromBB(AABB bb) {
        return Math.abs(bb.maxX-bb.minX)*(bb.maxY-bb.minY)*(bb.maxZ-bb.minZ);
    }

    public static Vec3 getRayTrace(Player player, double reachDistance) {
        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPosition = eyePosition.add(lookVector.scale(reachDistance));

        // Perform an entity ray trace
        AABB box = new AABB(eyePosition, endPosition).inflate(1.0); // Small buffer for hit detection
        List<Entity> entities = player.level().getEntities(player, box, e -> !e.isSpectator());

        Entity closestEntity = null;
        double closestDistance = reachDistance;
        Vec3 finalVec = null;

        for (Entity entity : entities) {
            AABB entityBB = entity.getBoundingBox();
            Optional<Vec3> hit = entityBB.clip(eyePosition, endPosition);

            if (hit.isPresent()) {
                double distance = eyePosition.distanceTo(hit.get());
                if (distance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distance;

                }
            }
        }

        finalVec = player.getLookAngle().multiply(closestDistance, closestDistance, closestDistance).add(0, player.getEyeHeight(), 0).add(player.position());

        return finalVec; // Returns the closest entity hit
    }

    public static Entity getEntityRayTrace(Player player, double reachDistance) {
        Vec3 eyePosition = player.getEyePosition(1.0F);
        Vec3 lookVector = player.getLookAngle();
        Vec3 endPosition = eyePosition.add(lookVector.scale(reachDistance));

        // Perform an entity ray trace
        AABB box = new AABB(eyePosition, endPosition).inflate(1.0); // Small buffer for hit detection
        List<Entity> entities = player.level().getEntities(player, box, e -> !e.isSpectator());

        Entity closestEntity = null;
        double closestDistance = reachDistance;

        for (Entity entity : entities) {
            AABB entityBB = entity.getBoundingBox();
            Optional<Vec3> hit = entityBB.clip(eyePosition, endPosition);

            if (hit.isPresent()) {
                double distance = eyePosition.distanceTo(hit.get());
                if (distance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        return closestEntity; // Returns the closest entity hit
    }

}
