package me.juancarloscp52.bedrockify.client.features.eatingAnimations;

import net.minecraft.util.Hand;

import java.util.Optional;

public interface IEatingState {
    default void setEatingHand(Hand hand) {
    }

    default Optional<Hand> getEatingHand() {
        return Optional.empty();
    }
}
