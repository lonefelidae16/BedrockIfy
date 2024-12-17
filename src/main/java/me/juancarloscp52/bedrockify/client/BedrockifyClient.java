package me.juancarloscp52.bedrockify.client;

import com.google.gson.Gson;
import me.juancarloscp52.bedrockify.Bedrockify;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockBlockShading;
import me.juancarloscp52.bedrockify.client.features.bedrockShading.BedrockSunGlareShading;
import me.juancarloscp52.bedrockify.client.features.fishingBobber.FishingBobber3DModel;
import me.juancarloscp52.bedrockify.client.features.heldItemTooltips.HeldItemTooltips;
import me.juancarloscp52.bedrockify.client.features.hudOpacity.HudOpacity;
import me.juancarloscp52.bedrockify.client.features.reacharoundPlacement.ReachAroundPlacement;
import me.juancarloscp52.bedrockify.client.features.sheepColors.SheepSkinResource;
import me.juancarloscp52.bedrockify.client.features.worldColorNoise.WorldColorNoiseSampler;
import me.juancarloscp52.bedrockify.client.gui.Overlay;
import me.juancarloscp52.bedrockify.client.gui.SettingsGUI;
import me.juancarloscp52.bedrockify.common.block.cauldron.BedrockCauldronBehavior;
import me.juancarloscp52.bedrockify.common.block.entity.WaterCauldronBlockEntity;
import me.juancarloscp52.bedrockify.common.features.cauldron.BedrockCauldronBlocks;
import me.juancarloscp52.bedrockify.common.payloads.CauldronParticlePayload;
import me.juancarloscp52.bedrockify.common.payloads.EatParticlePayload;
import me.juancarloscp52.bedrockify.mixin.featureManager.MixinFeatureManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class BedrockifyClient implements ClientModInitializer {

    private static BedrockifyClient instance;
    public static final Logger LOGGER = LogManager.getLogger();
    public ReachAroundPlacement reachAroundPlacement;
    public Overlay overlay;
    public HeldItemTooltips heldItemTooltips;
    public SettingsGUI settingsGUI;
    public WorldColorNoiseSampler worldColorNoiseSampler;
    public BedrockBlockShading bedrockBlockShading;
    public BedrockSunGlareShading bedrockSunGlareShading;
    public HudOpacity hudOpacity;
    public long deltaTime = 0;
    private int timeFlying = 0;
    private static KeyBinding keyBinding;

    public BedrockifyClientSettings settings;

    public static BedrockifyClient getInstance() {
        return instance;
    }
    @Override
    public void onInitializeClient() {
        instance = this;
        loadSettings();
        LOGGER.info("Initializing BedrockIfy Client.");
        overlay = new Overlay((MinecraftClient.getInstance()));
        reachAroundPlacement = new ReachAroundPlacement(MinecraftClient.getInstance());
        heldItemTooltips = new HeldItemTooltips();
        settingsGUI=new SettingsGUI();
        worldColorNoiseSampler = new WorldColorNoiseSampler();
        bedrockBlockShading = new BedrockBlockShading();
        bedrockSunGlareShading = new BedrockSunGlareShading();
        hudOpacity = new HudOpacity();
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("bedrockIfy.key.settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "BedrockIfy"));

        // Register 3D Bobber Entity.
        EntityModelLayerRegistry.registerModelLayer(FishingBobber3DModel.MODEL_LAYER, FishingBobber3DModel::generateModel);

        // Register the Color Tint of Potion-filled and Colored Cauldron Block if enabled.
        if (MixinFeatureManager.features.get(MixinFeatureManager.FEAT_CAULDRON)) {
            ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
                if (world == null || pos == null) {
                    return -1;
                }

                final Optional<WaterCauldronBlockEntity> entity = world.getBlockEntity(pos, BedrockCauldronBlocks.WATER_CAULDRON_ENTITY);
                return entity.map(WaterCauldronBlockEntity::getTintColor).orElse(-1);
            }, BedrockCauldronBlocks.POTION_CAULDRON, BedrockCauldronBlocks.COLORED_WATER_CAULDRON);

            // Lazy initialization of Bedrock's cauldron behavior after all the registries/tags are ready.
            ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
                BedrockCauldronBehavior.registerBehavior();
            });
        }

        // Register sheared sheep texture dynamically.
        if (!FabricLoader.getInstance().isModLoaded("optifabric")) {
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SheepSkinResource());
        }


        ClientPlayNetworking.registerGlobalReceiver(Bedrockify.EAT_PARTICLE_PAYLOAD.getId(), new EatParticlePayload.EatParticleHandler());

        ClientPlayNetworking.registerGlobalReceiver(Bedrockify.CAULDRON_PARTICLE_PAYLOAD.getId(), new CauldronParticlePayload.CauldronParticleHandler());

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> BedrockifyClient.getInstance().overlay.renderOverlay(drawContext));
        ClientTickEvents.END_CLIENT_TICK.register(client-> {
            while (keyBinding.wasPressed()){
                client.setScreen(settingsGUI.getConfigScreen(client.currentScreen));
            }
            hudOpacity.tick();
            bedrockSunGlareShading.tick(client.getRenderTickCounter().getTickDelta(true));

            // Stop flying drift
            if(settings.disableFlyingMomentum && null != client.player && client.player.getAbilities().flying){
                if(!(client.options.leftKey.isPressed() || client.options.backKey.isPressed() ||client.options.rightKey.isPressed() ||client.options.forwardKey.isPressed())){
                    client.player.setVelocity(0,client.player.getVelocity().getY(),0);
                }
                if(!(client.options.sneakKey.isPressed()|| client.options.jumpKey.isPressed())){
                    client.player.setVelocity(client.player.getVelocity().getX(), 0,client.player.getVelocity().getZ());

                }
            }

            // Stop elytra flying by pressing space
            if(null != client.player && settings.elytraStop && client.player.getPose().equals(EntityPose.GLIDING) && timeFlying > 10 && client.options.jumpKey.isPressed()){
                client.player.getAbilities().flying = false;
                client.player.sendAbilitiesUpdate();
                client.player.networkHandler.sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            }
            if(null != client.player && client.player.getPose().equals(EntityPose.GLIDING) && !client.options.jumpKey.isPressed())
                timeFlying++;
            else
                timeFlying = 0;

        });
        LOGGER.info("Initialized BedrockIfy Client");
    }

    public void loadSettings() {
        File file = new File("./config/bedrockify/bedrockifyClient.json");
        Gson gson = new Gson();
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                settings = gson.fromJson(fileReader, BedrockifyClientSettings.class);
                fileReader.close();
            } catch (IOException e) {
                LOGGER.warn("Could not load bedrockIfy settings: {}", e.getLocalizedMessage());
            }
        } else {
            settings = new BedrockifyClientSettings();
        }
    }

    public void saveSettings() {
        Gson gson = new Gson();
        File file = new File("./config/bedrockify/bedrockifyClient.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(settings));
            fileWriter.close();
        } catch (IOException e) {
            LOGGER.warn("Could not save bedrockIfy settings: {}", e.getLocalizedMessage());
        }
    }
}
