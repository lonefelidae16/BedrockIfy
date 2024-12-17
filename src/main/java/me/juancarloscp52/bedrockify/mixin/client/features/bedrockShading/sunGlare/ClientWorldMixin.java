package me.juancarloscp52.bedrockify.mixin.client.features.bedrockShading.sunGlare;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    @Shadow
    private @Final MinecraftClient client;

    /**
     * Modify the Sky color based on Camera angle.
     *
     * @return modified sky color
     */
    @ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    private int bedrockify$modifySkyColor(int original, @Local(ordinal = 0, argsOnly = true) float tickDelta) {
        final BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        if (!sunGlareShading.shouldApplyShading() || this.client.world == null) {
            return original;
        }

        final float rainGradient = this.client.world.getRainGradient(tickDelta);
        final float angleDiff = sunGlareShading.getSunAngleDiff();

        // Closer to the Sun, Darken the Sky, based on camera angle. Use a different multiplier for each channel in order to better match bedrock edition sky color.
        final float multiplierBlue = MathHelper.clampedLerp(sunGlareShading.getSkyAttenuation(), 1f, angleDiff + rainGradient);
        if (MathHelper.approximatelyEquals(multiplierBlue, 1f)) {
            return original;
        }
        final float multiplierRed = MathHelper.clampedLerp(sunGlareShading.getSkyAttenuation()-0.16f, 1f, angleDiff + rainGradient);
        final float multiplierGreen = MathHelper.clampedLerp(sunGlareShading.getSkyAttenuation()-0.06f, 1f, angleDiff + rainGradient);

        Vec3d color = Vec3d.unpackRgb(original);

        return ColorHelper.getArgb(color.multiply(multiplierRed, multiplierGreen, multiplierBlue));
    }

    @ModifyReturnValue(method = "getCloudsColor", at = @At("RETURN"))
    private int bedrockify$modifyCloudsColor(int original) {
        BedrockSunGlareShading sunGlareShading = BedrockifyClient.getInstance().bedrockSunGlareShading;
        Vec3d color = Vec3d.unpackRgb(original);
        return ColorHelper.getArgb(color.multiply(MathHelper.clampedLerp(0.8d, 1.0d, sunGlareShading.getSunRadiusDelta())));
    }

}
