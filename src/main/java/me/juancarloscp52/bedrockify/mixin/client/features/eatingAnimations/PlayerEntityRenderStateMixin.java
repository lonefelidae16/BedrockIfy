package me.juancarloscp52.bedrockify.mixin.client.features.eatingAnimations;

import me.juancarloscp52.bedrockify.client.features.eatingAnimations.IEatingState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(PlayerEntityRenderState.class)
public abstract class PlayerEntityRenderStateMixin implements IEatingState {
    @Unique
    private Hand eatingHand = null;

    @Override
    public void setEatingHand(Hand hand) {
        this.eatingHand = hand;
    }

    @Override
    public Optional<Hand> getEatingHand() {
        return Optional.ofNullable(this.eatingHand);
    }
}
