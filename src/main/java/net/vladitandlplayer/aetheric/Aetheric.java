package net.vladitandlplayer.aetheric;

import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import net.vladitandlplayer.aetheric.client.ModRenderer;
import net.vladitandlplayer.aetheric.item.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Aetheric implements ModInitializer {
	public static final String MOD_ID = "aetheric";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier path(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModItems.registerModItems();

		VeilEventPlatform.INSTANCE.onVeilRenderLevelStage(((stage, levelRenderer, bufferSource,
															matrixStack, frustumMatrix, projectionMatrix, i,
															renderTickCounter, camera, frustum) -> {
			if (stage == VeilRenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
				ModRenderer.renderHallucinations(levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, camera);
			}
		}));
	}
}