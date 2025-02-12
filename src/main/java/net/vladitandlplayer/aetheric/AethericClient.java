package net.vladitandlplayer.aetheric;

import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.vladitandlplayer.aetheric.client.ModRenderer;

public class AethericClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(((stage, levelRenderer, bufferSource,
                                                            matrixStack, frustumMatrix, projectionMatrix, i,
                                                            renderTickCounter, camera, frustum) -> {
            Aetheric.LOGGER.debug("Rendering stage: {}", stage);

            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
                ModRenderer.renderHallucinations(levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, camera);
            }
        }));
    }
}
