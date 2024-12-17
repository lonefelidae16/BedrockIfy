package me.juancarloscp52.bedrockify.mixin.client.features.loadingScreens;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.features.loadingScreens.LoadingScreenWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProgressScreen.class)
public class ProgressScreenMixin extends Screen {


    @Shadow private Text task;
    @Shadow private int progress;
    protected ProgressScreenMixin(Text title) {
        super(title);
    }

    /**
     * Renders the loading screen widgets with progress bar if necessary.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"), cancellable = true)
    public void renderLoadScreen(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if(!BedrockifyClient.getInstance().settings.isLoadingScreenEnabled() || client == null){
            return;
        }
        super.render(context, mouseX, mouseY, delta);
        if (title != null) {
            if (this.task != null && this.progress != 0) {
                LoadingScreenWidget.getInstance().render(context, client.getWindow().getScaledWidth() / 2, client.getWindow().getScaledHeight() / 2, this.title, this.task, this.progress);
            } else {
                LoadingScreenWidget.getInstance().render(context, client.getWindow().getScaledWidth() / 2, client.getWindow().getScaledHeight() / 2, this.title, null, -1);
            }
        } else if (this.task != null && this.progress != 0) {
            LoadingScreenWidget.getInstance().render(context, client.getWindow().getScaledWidth() / 2, client.getWindow().getScaledHeight() / 2, this.task, null, this.progress);
        } else {
            LoadingScreenWidget.getInstance().render(context, client.getWindow().getScaledWidth() / 2, client.getWindow().getScaledHeight() / 2, Text.literal(""), null, -1);
        }

        info.cancel();
    }

}
