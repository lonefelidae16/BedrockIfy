package me.juancarloscp52.bedrockify.mixin.client.features.hudOpacity.appleskin;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import squeek.appleskin.client.HUDOverlayHandler;

@Pseudo
@Mixin(HUDOverlayHandler.class)
public class HUDOverlayHandlerMixin {
    @ModifyVariable(method = "drawHungerOverlay(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/MinecraftClient;IIFZI)V", at = @At("HEAD"),ordinal = 0)
    public float clampAlphaHungerOverlay(float alpha){
        return BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)*alpha;
    }
    @ModifyVariable(method = "drawHealthOverlay(Lnet/minecraft/client/gui/DrawContext;FFLnet/minecraft/client/MinecraftClient;IIFI)V", at = @At("HEAD"),ordinal = 2)
    public float clampAlphaHealthOverlay(float alpha){
        return BedrockifyClient.getInstance().hudOpacity.getHudOpacity(false)*alpha;
    }

}