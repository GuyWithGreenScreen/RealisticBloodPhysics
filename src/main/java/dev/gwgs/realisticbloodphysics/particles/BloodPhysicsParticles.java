package dev.gwgs.realisticbloodphysics.particles;

import dev.gwgs.realisticbloodphysics.Realisticbloodphysics;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BloodPhysicsParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Realisticbloodphysics.MODID);

    public static final Supplier<SimpleParticleType> BLOOD_PARTICLES =
            PARTICLE_TYPES.register("blood_particle", () -> new SimpleParticleType(true));

    public static final Supplier<SimpleParticleType> BONE_PARTICLES =
            PARTICLE_TYPES.register("bone_particle", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
