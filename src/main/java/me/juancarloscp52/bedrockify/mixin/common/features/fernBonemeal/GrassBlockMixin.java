package me.juancarloscp52.bedrockify.mixin.common.features.fernBonemeal;

import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.features.fernBonemeal.FernBonemeal;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GrassBlock.class)
public class GrassBlockMixin {
    @ModifyVariable(method = "grow",at=@At("STORE"), ordinal = 0)
    public RegistryEntry<?> addFern(RegistryEntry<?> registryEntry, ServerWorld world, Random random, BlockPos pos, BlockState state) {

        if(!Bedrockify.getInstance().settings.fernBonemeal)
            return registryEntry;

        if(registryEntry.getKey().isPresent() && registryEntry.getKey().get() instanceof RegistryKey<?> key){
            RegistryKey<Biome> biome = world.getBiome(pos).getKey().orElseThrow();
            if(biome.equals(BiomeKeys.TAIGA) || biome.equals(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA) || biome.equals(BiomeKeys.SNOWY_TAIGA) || biome.equals(BiomeKeys.OLD_GROWTH_PINE_TAIGA) || biome.equals(BiomeKeys.JUNGLE) || biome.equals(BiomeKeys.BAMBOO_JUNGLE) || biome.equals(BiomeKeys.SPARSE_JUNGLE)) {
                if (key.getValue().getPath().equals("grass_bonemeal") && random.nextInt(4) == 0) {
                    return world.getRegistryManager().getOrThrow(RegistryKeys.PLACED_FEATURE).getEntry(FernBonemeal.SINGLE_PIECE_OF_FERN_PLACED.getValue()).orElse(null);
                }
            }
        }

        return registryEntry;
    }

}
