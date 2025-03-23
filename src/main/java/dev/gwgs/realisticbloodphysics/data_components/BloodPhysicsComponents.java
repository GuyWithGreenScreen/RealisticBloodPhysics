package dev.gwgs.realisticbloodphysics.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gwgs.realisticbloodphysics.Realisticbloodphysics;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.List;
import java.util.function.UnaryOperator;



public class BloodPhysicsComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE,Realisticbloodphysics.MODID);

    // Custom Codec for Vec3 (as Minecraft does not provide one)
    public static final Codec<Vec3> VEC3_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter(Vec3::x),
            Codec.DOUBLE.fieldOf("y").forGetter(Vec3::y),
            Codec.DOUBLE.fieldOf("z").forGetter(Vec3::z)
    ).apply(instance, Vec3::new));

    // Codec for HashMap<Vec3, Integer>
    public static final Codec<HashMap<Vec3, Integer>> HASHMAP_CODEC = Codec.unboundedMap(VEC3_CODEC, Codec.INT)
            .xmap(HashMap::new, map -> map);


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HashMap<Vec3, Integer>>> BLOOD_LOCATION = register(
            "blood_location", builder -> builder.persistent(HASHMAP_CODEC));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                          UnaryOperator<DataComponentType.Builder<T>> builderUnaryOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderUnaryOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
