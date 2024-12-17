package me.juancarloscp52.bedrockify.mixin.common.features.fireAspect;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.features.fireAspectLight.FireAspectLightHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @ModifyExpressionValue(method = "interact",at=@At(value = "INVOKE",target = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult interact(ActionResult original, Entity entity, Hand hand){
        PlayerEntity $this = PlayerEntity.class.cast(this);
        if(entity instanceof TntMinecartEntity tntMinecart && Bedrockify.getInstance().settings.fireAspectLight && $this.getAbilities().allowModifyWorld){
            ItemStack itemStack = $this.getStackInHand(hand);
            if(!tntMinecart.isPrimed() && (FireAspectLightHelper.canLitWith(itemStack) || (itemStack.isOf(Items.FLINT_AND_STEEL) || itemStack.isOf(Items.FIRE_CHARGE)))){
                tntMinecart.prime();
                itemStack.damage(1, $this, LivingEntity.getSlotForHand(hand));
                $this.getWorld().playSound($this, $this.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, $this.getWorld().getRandom().nextFloat() * 0.4F + 0.8F);
                return ActionResult.SUCCESS;
            }
        }
        return original;
    }

}
