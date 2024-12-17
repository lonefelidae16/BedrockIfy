package me.juancarloscp52.bedrockify.mixin.client.features.sheepColors;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.sheepColors.SheepSkinResource;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepWoolFeatureRenderer.class)
public abstract class SheepWoolFeatureRendererMixin extends FeatureRenderer<SheepEntityRenderState, SheepEntityModel> {
    public SheepWoolFeatureRendererMixin(FeatureRendererContext<SheepEntityRenderState, SheepEntityModel> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/SheepEntityRenderState;FF)V", at = @At("RETURN"))
    private void bedrockify$renderWoolColorAfterShearing(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, SheepEntityRenderState sheepState, float tickDelta, float animationProgress, CallbackInfo ci) {
        if (!BedrockifyClient.getInstance().settings.sheepColors) {
            return;
        }

        final Model sheepModel = this.getContextModel();
        final int color;
        if (sheepState.customName != null && "jeb_".equals(sheepState.customName.getString())) {
            final int baseColorId = (int) (sheepState.age / 25 + sheepState.id);
            final int colorLength = DyeColor.values().length;
            final int currentColorId = baseColorId % colorLength;
            final int nextColorId = (baseColorId + 1) % colorLength;
            float gradientDelta = ((sheepState.age % 25) + tickDelta) / 25.0f;
            int currentColor = SheepEntity.getRgbColor(DyeColor.byId(currentColorId));
            int nextColor = SheepEntity.getRgbColor(DyeColor.byId(nextColorId));
            color = ColorHelper.lerp(gradientDelta, currentColor, nextColor);
        } else {
            color = SheepEntity.getRgbColor(sheepState.color);
        }
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(sheepModel.getLayer(SheepSkinResource.TEXTURE_SHEARED));
        sheepModel.render(matrixStack, vertexConsumer, light, LivingEntityRenderer.getOverlay(sheepState, 0.075f), color);
    }
}
