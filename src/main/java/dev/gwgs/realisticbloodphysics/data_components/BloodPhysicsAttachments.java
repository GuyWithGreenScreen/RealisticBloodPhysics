package dev.gwgs.realisticbloodphysics.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.gwgs.realisticbloodphysics.Realisticbloodphysics;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BloodPhysicsAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Realisticbloodphysics.MODID);

    // Define Vec4 class (since Forge doesn't have one)
    public static class Vec4 {
        public final double x, y, z, w;

        public Vec4(double x, double y, double z, double w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }

        public Vec4 add(Vec3 position) {
            return new Vec4(position.x, position.y, position.z, this.w);
        }
    }

    // Custom Codec for Vec4
    public static final Codec<Vec4> VEC4_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("x").forGetter(v -> v.x),
            Codec.DOUBLE.fieldOf("y").forGetter(v -> v.y),
            Codec.DOUBLE.fieldOf("z").forGetter(v -> v.z),
            Codec.DOUBLE.fieldOf("w").forGetter(v -> v.w)
    ).apply(instance, Vec4::new));

    // Wrapper class for storing blood location data
    public static class BloodDataEntry {
        private final Vec4 position;
        private final int amount;

        public BloodDataEntry(Vec4 position, int amount) {
            this.position = position;
            this.amount = amount;
        }

        public Vec4 getPosition() {
            return position;
        }

        public int getAmount() {
            return amount;
        }

        // Codec for serialization
        public static final Codec<BloodDataEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                VEC4_CODEC.fieldOf("position").forGetter(BloodDataEntry::getPosition),
                Codec.INT.fieldOf("amount").forGetter(BloodDataEntry::getAmount)
        ).apply(instance, BloodDataEntry::new));
    }

    // Wrapper class for managing blood data
    public static class BloodMap {
        private final HashMap<Vec4, Integer> bloodData;
        private final double rotation;
        private final double spreadFactor;

        public BloodMap(HashMap<Vec4, Integer> bloodData, double rotation, double spreadFactor) {
            this.bloodData = bloodData;
            this.rotation = rotation;
            this.spreadFactor = spreadFactor;
        }

        public HashMap<Vec4, Integer> getBloodData() {
            return bloodData;
        }

        public double getRotation() {
            return rotation;
        }

        public double getSpreadFactor() {
            return spreadFactor;
        }

        // Convert Map to List for Serialization
        public List<BloodDataEntry> toList() {
            return bloodData.entrySet().stream()
                    .map(entry -> new BloodDataEntry(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        }

        // Convert List back to Map
        public static BloodMap fromList(List<BloodDataEntry> list, double decayRate, double spreadFactor) {
            HashMap<Vec4, Integer> map = new HashMap<>();
            for (BloodDataEntry entry : list) {
                map.put(entry.getPosition(), entry.getAmount());
            }
            return new BloodMap(map, decayRate, spreadFactor);
        }

        // Codec for BloodMap
        public static final Codec<BloodMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BloodDataEntry.CODEC.listOf().fieldOf("bloodData").forGetter(BloodMap::toList),
                Codec.DOUBLE.fieldOf("rotation").forGetter(BloodMap::getRotation),
                Codec.DOUBLE.fieldOf("spreadFactor").forGetter(BloodMap::getSpreadFactor)
        ).apply(instance, BloodMap::fromList));
    }

    // Registering the Blood Location Attachment
    public static final Supplier<AttachmentType<BloodMap>> BLOOD_LOCATION = ATTACHMENT_TYPES.register(
            "blood_location", () -> AttachmentType.<BloodMap>builder(() -> new BloodMap(new HashMap<>(), 1.0, 0.5))
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
