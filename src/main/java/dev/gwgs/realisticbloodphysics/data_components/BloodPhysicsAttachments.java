package dev.gwgs.realisticbloodphysics.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gwgs.realisticbloodphysics.Realisticbloodphysics;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dev.gwgs.realisticbloodphysics.data_components.BloodPhysicsComponents.HASHMAP_CODEC;

public class BloodPhysicsAttachments {
    // Deferred Register for Attachment Types
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Realisticbloodphysics.MODID);

    // Custom Codec for Vec3
    public static final Codec<Vec3> VEC3_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter(Vec3::x),
            Codec.DOUBLE.fieldOf("y").forGetter(Vec3::y),
            Codec.DOUBLE.fieldOf("z").forGetter(Vec3::z)
    ).apply(instance, Vec3::new));

    // Wrapper class for storing blood location data
    public static class BloodDataEntry {
        private final Vec3 position;
        private final int amount;

        public BloodDataEntry(Vec3 position, int amount) {
            this.position = position;
            this.amount = amount;
        }

        public Vec3 getPosition() {
            return position;
        }

        public int getAmount() {
            return amount;
        }

        // Codec for serialization
        public static final Codec<BloodDataEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                VEC3_CODEC.fieldOf("position").forGetter(BloodDataEntry::getPosition),
                Codec.INT.fieldOf("amount").forGetter(BloodDataEntry::getAmount)
        ).apply(instance, BloodDataEntry::new));
    }

    // Wrapper class for managing blood data
    public static class BloodMap {
        private final HashMap<Vec3, Integer> bloodData;

        public BloodMap(HashMap<Vec3, Integer> bloodData) {
            this.bloodData = bloodData;
        }

        public HashMap<Vec3, Integer> getBloodData() {
            return bloodData;
        }

        // Convert Map to List for Serialization
        public List<BloodDataEntry> toList() {
            return bloodData.entrySet().stream()
                    .map(entry -> new BloodDataEntry(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        }

        // Convert List back to Map
        public static BloodMap fromList(List<BloodDataEntry> list) {
            HashMap<Vec3, Integer> map = new HashMap<>();
            for (BloodDataEntry entry : list) {
                map.put(entry.getPosition(), entry.getAmount());
            }
            return new BloodMap(map);
        }

        // Codec for BloodMap (Using List Instead of HashMap)
        public static final Codec<BloodMap> CODEC = BloodDataEntry.CODEC.listOf()
                .xmap(BloodMap::fromList, BloodMap::toList);
    }

    // Default value for the attachment (prevents null issues)
    private static final Supplier<HashMap<Vec3, Integer>> DEFAULT_VALUE_SUPPLIER = HashMap::new;

    // Registering the Blood Location Attachment
    public static final Supplier<AttachmentType<BloodMap>> BLOOD_LOCATION = ATTACHMENT_TYPES.register(
            "blood_location", () -> AttachmentType.<BloodMap>builder(() -> new BloodMap(new HashMap<>()))  // Fresh instance per entity
                    .serialize(BloodMap.CODEC)
                    .build()
    );

    public static final Supplier<AttachmentType<Integer>> BLOOD = ATTACHMENT_TYPES.register(
            "blood", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );

    // Register Attachments to the Event Bus
    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}