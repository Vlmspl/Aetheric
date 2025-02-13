package net.vladitandlplayer.aetheric;

import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.vladitandlplayer.aetheric.client.ModRenderer;

public class AethericClient implements ClientModInitializer {

    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(Aetheric.MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(((stage, levelRenderer, bufferSource,
                                                            matrixStack, frustumMatrix, projectionMatrix, i,
                                                            renderTickCounter, camera, frustum) -> {

            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
                ModRenderer.renderHallucinations(levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, camera);
            }
        }));
    }
}
