package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.systems.RenderSystem;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.render.SkyRendering;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRendering.class)
public abstract class SkyRenderingMixin {
    /**
     * Modify the Sun radius from stored delta.<br>
     * Original radius is <code>30.0F</code>.
     */
    @ModifyExpressionValue(method = "renderSun", at = {
            @At(value = "CONSTANT", args = "floatValue=30.0f"),
            @At(value = "CONSTANT", args = "floatValue=-30.0f")
    })
    private float bedrockify$modifySunRadius(float original) {
        BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        if (!sunGlareShading.shouldApplyShading() || sunGlareShading.getSunRadiusDelta() >= 1f) {
            return original;
        }

        return MathHelper.clampedLerp(original * 1.3f, original, sunGlareShading.getSunRadiusDelta());
    }

    @Inject(method = "renderCelestialBodies", at = @At("HEAD"))
    private void bedrockify$modifySunIntensity(CallbackInfo ci) {
        BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        if (!sunGlareShading.shouldApplyShading() || sunGlareShading.getSunRadiusDelta() >= 1f) {
            return;
        }

        float value = MathHelper.clampedLerp(2.0f, 1.0f, sunGlareShading.getSunRadiusDelta());

        RenderSystem.setShaderColor(value, value, value, 1.0f);
    }

    @Inject(method = "renderCelestialBodies", at = @At("RETURN"))
    private void bedrockify$restoreShaderColor(CallbackInfo ci) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1.0f);
    }
}
