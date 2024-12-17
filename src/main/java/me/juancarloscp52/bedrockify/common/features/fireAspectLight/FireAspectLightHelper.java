package me.juancarloscp52.bedrockify.common.features.fireAspectLight;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

public final class FireAspectLightHelper {
    public static boolean canLitWith(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (itemStack.isEmpty()) {
            return false;
        }

        return EnchantmentHelper.getEnchantments(itemStack).getEnchantments().stream().anyMatch(e -> e.matchesId(Enchantments.FIRE_ASPECT.getValue()));
    }
}
