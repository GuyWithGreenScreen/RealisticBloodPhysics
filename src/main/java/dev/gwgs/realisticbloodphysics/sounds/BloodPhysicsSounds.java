package dev.gwgs.realisticbloodphysics.sounds;

import dev.gwgs.realisticbloodphysics.Realisticbloodphysics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BloodPhysicsSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Realisticbloodphysics.MODID);

    public static final Supplier<SoundEvent> BloodHit = registerSoundEvent("blood");
    public static final Supplier<SoundEvent> BoneHitSoft = registerSoundEvent("bone_soft");
    public static final Supplier<SoundEvent> BoneHitHard = registerSoundEvent("bone_hard");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Realisticbloodphysics.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
