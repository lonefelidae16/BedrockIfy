package me.juancarloscp52.bedrockify.common.features.cauldron;

import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.block.ColoredWaterCauldronBlock;
import me.juancarloscp52.bedrockify.common.block.PotionCauldronBlock;
import me.juancarloscp52.bedrockify.common.block.entity.WaterCauldronBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class BedrockCauldronBlocks {
    public static final Block POTION_CAULDRON;
    public static final Block COLORED_WATER_CAULDRON;

    private static final Identifier ID_POTION_CAULDRON = Identifier.of(Bedrockify.MOD_ID, "potion_cauldron");
    private static final Identifier ID_COLORED_WATER_CAULDRON = Identifier.of(Bedrockify.MOD_ID, "colored_water_cauldron");

    public static final BlockEntityType<WaterCauldronBlockEntity> WATER_CAULDRON_ENTITY;

    public static void register() {
        Registry.register(Registries.BLOCK, ID_POTION_CAULDRON, POTION_CAULDRON);
        Registry.register(Registries.BLOCK, ID_COLORED_WATER_CAULDRON, COLORED_WATER_CAULDRON);

        Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Bedrockify.MOD_ID, "water_cauldron_entity"), WATER_CAULDRON_ENTITY);
    }

    private static Block prepare(Identifier id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        return factory.apply(settings.registryKey(key));
    }

    static {
        POTION_CAULDRON = prepare(ID_POTION_CAULDRON, PotionCauldronBlock::new, AbstractBlock.Settings.copy(Blocks.CAULDRON).emissiveLighting((state, world, pos) -> true));
        COLORED_WATER_CAULDRON = prepare(ID_COLORED_WATER_CAULDRON, ColoredWaterCauldronBlock::new, AbstractBlock.Settings.copy(Blocks.CAULDRON));

        WATER_CAULDRON_ENTITY = FabricBlockEntityTypeBuilder.create(WaterCauldronBlockEntity::new, POTION_CAULDRON, COLORED_WATER_CAULDRON).build();
    }
}
