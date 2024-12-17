package me.juancarloscp52.bedrockify.mixin.client.features.babyVillagerHeads;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerResemblingModel.class)
public abstract class VillagerResemblingModelMixin {
    @Shadow @Final private ModelPart head;

    @Inject(method = "setAngles", at = @At("RETURN"))
    private void bedrockify$customBabyHeadScale(VillagerEntityRenderState villagerEntityRenderState, CallbackInfo ci) {
        if (villagerEntityRenderState.baby && BedrockifyClient.getInstance().settings.babyVillagerHeads) {
            this.head.scale(new Vector3f(0.5f));
        }
    }
}
