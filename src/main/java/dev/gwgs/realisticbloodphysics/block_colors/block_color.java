package dev.gwgs.realisticbloodphysics.block_colors;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.jetbrains.annotations.Nullable;

//@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class block_color {

    //@SubscribeEvent
    public static void blockTint(RegisterColorHandlersEvent.Block event) {
        event.register(new BlockColor() {
            @Override
            public int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int i) {
                return 0xFFFF0000;
            }
        }, Blocks.GRASS_BLOCK);
    }
}
