package me.juancarloscp52.bedrockify.mixin.client.features.eatingAnimations;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.eatingAnimations.IEatingState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private void bedrockify$storeEatingState(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if (!BedrockifyClient.getInstance().settings.isEatingAnimationsEnabled() || !(playerEntityRenderState instanceof IEatingState state)) {
            return;
        }

        ItemStack mainHandStack = abstractClientPlayerEntity.getMainHandStack();
        ItemStack offHandStack = abstractClientPlayerEntity.getOffHandStack();
        if (bedrockify$checkEatingAction(playerEntityRenderState, Hand.MAIN_HAND, mainHandStack)) {
            state.setEatingHand(Hand.MAIN_HAND);
        } else if (bedrockify$checkEatingAction(playerEntityRenderState, Hand.OFF_HAND, offHandStack)) {
            state.setEatingHand(Hand.OFF_HAND);
        } else {
            state.setEatingHand(null);
        }
    }

    @Unique
    private boolean bedrockify$checkEatingAction(PlayerEntityRenderState state, Hand hand, ItemStack itemStack) {
        return state.itemUseTimeLeft > 0 && state.activeHand == hand && (itemStack.getUseAction() == UseAction.EAT || itemStack.getUseAction() == UseAction.DRINK);
    }
}
