package me.juancarloscp52.bedrockify.mixin.common.features.fireAspect;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.common.features.fireAspectLight.FireAspectLightHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin {

    @Shadow
    public static void primeTnt(World world, BlockPos pos) {}

    @ModifyReturnValue(method = "onUseWithItem",at=@At("RETURN"))
    private ActionResult onUse(ActionResult original, ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if(!Bedrockify.getInstance().settings.fireAspectLight || !player.getAbilities().allowModifyWorld)
            return original;
        ItemStack itemStack = player.getStackInHand(hand);
        if(FireAspectLightHelper.canLitWith(itemStack)){
            primeTnt(world,pos);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            itemStack.damage(1, player, LivingEntity.getSlotForHand(hand));
            world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            return ActionResult.SUCCESS;
        }
        return original;
    }

}
