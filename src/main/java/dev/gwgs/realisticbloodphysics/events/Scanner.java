package dev.gwgs.realisticbloodphysics.events;


import dev.gwgs.realisticbloodphysics.Realisticbloodphysics;
import dev.gwgs.realisticbloodphysics.data_components.BloodPhysicsAttachments;
import dev.gwgs.realisticbloodphysics.data_components.BloodPhysicsComponents;
import dev.gwgs.realisticbloodphysics.particles.BloodPhysicsParticles;
import dev.gwgs.realisticbloodphysics.sounds.BloodPhysicsSounds;
import dev.gwgs.realisticbloodphysics.tools.Tools;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Realisticbloodphysics.MODID)
public class Scanner {

    @SubscribeEvent
    public static void mobBloodRegister(EntityEvent.EntityConstructing event) {
        if (!(event.getEntity() instanceof Player) && event.getEntity().getData(BloodPhysicsAttachments.BLOOD) == 0) {
            event.getEntity().setData(BloodPhysicsAttachments.BLOOD, (int) (Tools.getVolumeFromBB(event.getEntity().getBoundingBox()) *5000));
            System.out.println(event.getEntity().getName() + " 0 " + (int) (Tools.getVolumeFromBB(event.getEntity().getBoundingBox()) *5000));
        }
    }

    @SubscribeEvent
    public static void mobTickEvent(EntityTickEvent.Pre event) {
        if (event.getEntity().hasData(BloodPhysicsAttachments.BLOOD_LOCATION)) {
            if (!event.getEntity().getData(BloodPhysicsAttachments.BLOOD_LOCATION).getBloodData().isEmpty()) {
                HashMap<BloodPhysicsAttachments.Vec4, Integer> tempMap = event.getEntity().getData(BloodPhysicsAttachments.BLOOD_LOCATION).getBloodData();
                if (event.getEntity().getData(BloodPhysicsAttachments.BLOOD) <= 1) {
                    event.getEntity().kill();
                }
                for (BloodPhysicsAttachments.Vec4 vec3 : tempMap.keySet()) {
                    event.getEntity().setData(BloodPhysicsAttachments.BLOOD,
                            event.getEntity().getData(BloodPhysicsAttachments.BLOOD) - tempMap.get(vec3));
                    if (!event.getEntity().level().isClientSide()) {
                        BloodPhysicsAttachments.Vec4 newVec = vec3.add(event.getEntity().position());
                        ((ServerLevel) event.getEntity().level()).sendParticles(BloodPhysicsParticles.BLOOD_PARTICLES.get(),
                                newVec.x, newVec.y, newVec.z, tempMap.get(vec3), 0, 0, 0,
                                Tools.random(-(((double) tempMap.get(vec3) /10)*3), -(((double) tempMap.get(vec3) /10)*3)));
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void PlayerHitEvent(LivingDamageEvent.Post event) {
        System.out.println(event.getSource().getEntity());
        if (event.getSource().getEntity() instanceof Player player) {
            System.out.println("TEAEEEEEEEEEEEEEEEEE");
            Vec3 pos;
            if (Tools.getEntityRayTrace(player, player.entityInteractionRange()) != null) {
                pos = Tools.getRayTrace(player, player.entityInteractionRange());
            } else {
                pos = event.getEntity().position();
            }
            Supplier<Integer> sup = () -> {
                if (event.getNewDamage() < 5) {
                    return 1;
                } else if (event.getNewDamage() < 8) {
                    return 2;
                } else if (event.getNewDamage() < 15) {
                    return 3;
                } else {
                    return 4;
                }
            };
            if (event.getEntity().getData(BloodPhysicsAttachments.BLOOD_LOCATION) != null) {
                System.out.println(event.getEntity().getData(BloodPhysicsAttachments.BLOOD_LOCATION));
                BloodPhysicsAttachments.BloodMap map = event.getEntity().getData(BloodPhysicsAttachments.BLOOD_LOCATION);
                map.getBloodData().put(Tools.vec3ToVec4(pos.subtract(event.getEntity().position()), event.getEntity().getYRot()), sup.get());
                event.getEntity().setData(BloodPhysicsAttachments.BLOOD_LOCATION, map);
                for (BloodPhysicsAttachments.Vec4 vec : event.getEntity().getData(BloodPhysicsAttachments.BLOOD_LOCATION).getBloodData().keySet()) {
                    System.out.println(vec + " - " + event.getEntity().getData(BloodPhysicsAttachments.BLOOD_LOCATION).getBloodData().get(vec));
                }
            } else {
                System.out.println("New");
                HashMap<BloodPhysicsAttachments.Vec4, Integer> tempMap = new HashMap<>();
                BloodPhysicsAttachments.BloodMap map = new BloodPhysicsAttachments.BloodMap(tempMap, event.getEntity().getXRot(), 0);
                map.getBloodData().put(Tools.vec3ToVec4(pos.subtract(event.getEntity().position()), event.getEntity().getYRot()), sup.get());
                event.getEntity().setData(BloodPhysicsAttachments.BLOOD_LOCATION, map);
            }
            player.level().playSound(null, pos.x, pos.y, pos.z, BloodPhysicsSounds.BloodHit.get(), SoundSource.NEUTRAL);
            for (int a = 0; a < 5 * ((int) (Math.clamp(event.getOriginalDamage() *4, 1, 100))); a++) {
                //System.out.println("attempt");
                if (!player.level().isClientSide()) {
                    ((ServerLevel)player.level()).sendParticles(BloodPhysicsParticles.BLOOD_PARTICLES.get(), pos.x, pos.y, pos.z, 1,  0, 0, 0, Tools.random(-0.5 * (Math.clamp(event.getOriginalDamage(), 1, 100) * 5), 0.5 * (Math.clamp(event.getOriginalDamage(), 1, 100) * 5)));
                }
            }
            if (event.getOriginalDamage() > 5 && event.getOriginalDamage() < 8) {
                player.level().playSound(null, pos.x, pos.y, pos.z, BloodPhysicsSounds.BoneHitSoft.get(), SoundSource.NEUTRAL);
                for (int a = 0; a < ((int)event.getOriginalDamage()/4); a++) {
                    //System.out.println("attempt");
                    if (!player.level().isClientSide()) {
                        ((ServerLevel)player.level()).sendParticles(BloodPhysicsParticles.BONE_PARTICLES.get(), pos.x, pos.y, pos.z, 1,  0, 0, 0, Tools.random(-0.5 * (Math.clamp(event.getOriginalDamage(), 1, 100) * 5), 0.5 * (Math.clamp(event.getOriginalDamage(), 1, 100) * 5)));
                    }
                }
            } else if (event.getOriginalDamage() > 8) {
                player.level().playSound(null, pos.x, pos.y, pos.z, BloodPhysicsSounds.BoneHitHard.get(), SoundSource.NEUTRAL);
                for (int a = 0; a < ((int)event.getOriginalDamage()/2); a++) {
                    //System.out.println("attempt");
                    if (!player.level().isClientSide()) {
                        ((ServerLevel)player.level()).sendParticles(BloodPhysicsParticles.BONE_PARTICLES.get(), pos.x, pos.y, pos.z, 1,  0, 0, 0, Tools.random(-0.5 * (Math.clamp(event.getOriginalDamage(), 1, 100) * 5), 0.5 * (Math.clamp(event.getOriginalDamage(), 1, 100) * 5)));
                    }
                }
            }
        }
    }

    public static void PlayerScanForEntityEvent(PlayerTickEvent.Pre event) {
        if (Tools.getEntityRayTrace(event.getEntity(), event.getEntity().entityInteractionRange()) == null) {
            event.getEntity().displayClientMessage(Component.literal("Not Detected!").withStyle(ChatFormatting.RED), true);
        } else {
            event.getEntity().displayClientMessage(Component.literal("Detected!").withStyle(ChatFormatting.GREEN), true);
            Vec3 pos = Tools.getRayTrace(event.getEntity(), event.getEntity().entityInteractionRange());
            for (int a = 0; a < 1; a++) {
                event.getEntity().level().addParticle(ParticleTypes.FLAME, pos.x - (event.getEntity().getLookAngle().x*a), pos.y - (event.getEntity().getLookAngle().y*a), pos.z - (event.getEntity().getLookAngle().z*a), 0, 0, 0);
            }
        }
        /*
        boolean seesEntity = false;
        Entity seenEntity;
        Player scanningPlayer = event.getEntity();
        Level level = event.getEntity().level();
        double reach = scanningPlayer.entityInteractionRange();
        Vec3 playerPos = scanningPlayer.position();
        for (int b = 0; b < reach*10; b++) {
            int i = b/10;
            //System.out.println(i);
            Vec3 currentPos = (scanningPlayer.getLookAngle().multiply(i, i ,i).add(scanningPlayer.position())).add(0, scanningPlayer.getEyeHeight(), 0);
            if (level.getBlockState(new BlockPos((int) Math.floor(currentPos.x),(int) Math.floor(currentPos.y),(int) Math.floor(currentPos.z))).isAir() || !level.getBlockState(new BlockPos((int) Math.floor(currentPos.x),(int) Math.floor(currentPos.y),(int) Math.floor(currentPos.z))).isCollisionShapeFullBlock(level, new BlockPos((int) Math.floor(currentPos.x),(int) Math.floor(currentPos.y),(int) Math.floor(currentPos.z)))) {
                //level.addParticle(ParticleTypes.FLAME, currentPos.x, currentPos.y, currentPos.z, 0, 0, 0);
                //System.out.println("te");
                for (Entity entity : level.getEntities(null, new AABB(-reach + playerPos.x, -reach + playerPos.y, -reach + playerPos.z, reach +playerPos.x, reach +playerPos.y, reach + playerPos.z))) {
                    if (entity != scanningPlayer) {
                        //System.out.println("test");
                        if (entity.getBoundingBox().contains(currentPos)) {
                            System.out.println("YES - " + entity.getName());
                            seenEntity = entity;
                            seesEntity = true;
                            for (int a = 0; a < 1; a++) {
                                level.addParticle(ParticleTypes.FLAME, currentPos.x - (scanningPlayer.getLookAngle().x*a), currentPos.y - (scanningPlayer.getLookAngle().y*a), currentPos.z - (scanningPlayer.getLookAngle().z*a), 0, 0, 0);
                            }
                            scanningPlayer.displayClientMessage(Component.literal("Detected!").withStyle(ChatFormatting.GREEN), true);
                            break;
                        } else {
                            scanningPlayer.displayClientMessage(Component.literal("Not Detected...").withStyle(ChatFormatting.RED), true);
                        }
                    }
                }
            } else {
                break;
            }
            if (seesEntity) {
                break;
            }
        }

         */

    }

}
