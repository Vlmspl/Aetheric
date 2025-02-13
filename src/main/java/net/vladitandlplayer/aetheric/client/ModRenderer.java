package net.vladitandlplayer.aetheric.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.Veil;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.FramebufferManager;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.vertex.VertexArray;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.vladitandlplayer.aetheric.Aetheric;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Matrix4fc;

import static org.lwjgl.opengl.GL11C.*;

@Environment(EnvType.CLIENT)
public class ModRenderer {
    private static final ResourceLocation HALLUCINATION_FBO = Aetheric.path("hallucination");
    private static final ResourceLocation HALLUCINATIONS_SHADER = Aetheric.path("hallucinations");
    private static VertexArray hallucinationsArray;

    public static void renderHallucinations(LevelRenderer levelRenderer,
                                            MultiBufferSource.BufferSource bufferSource,
                                            MatrixStack matrixStack,
                                            Matrix4fc frustumMatrix,
                                            Matrix4fc projectionMatrix,
                                            Camera camera) {
        if (VeilLevelPerspectiveRenderer.isRenderingPerspective() || !projectionMatrix.isFinite()) {
            Aetheric.LOGGER.info("not rendering perspective or projection is infinite");
            return;

        }

        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(HALLUCINATION_FBO);

        ShaderProgram shaderProgram = VeilRenderSystem.setShader(HALLUCINATIONS_SHADER);
        if (shaderProgram == null) {
            Aetheric.LOGGER.info("ShaderProgram you defined is null");
            return;
        }

        if (hallucinationsArray == null) {
            hallucinationsArray = VertexArray.create();
        }
        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX);

        builder.addVertex(-1, -1, 0);
        builder.setUv(-1, -1);
        builder.addVertex(-1, 1, 0);
        builder.setUv(-1, 1);
        builder.addVertex(1, -1, 0);
        builder.setUv(1, -1);

        hallucinationsArray.upload(builder.build(), VertexArray.DrawUsage.DYNAMIC);


        fbo.bind(true);
        shaderProgram.bind();
        hallucinationsArray.bind();


        hallucinationsArray.draw();
        ShaderProgram.unbind();
        AdvancedFbo.unbind();
        VertexArray.unbind();

    }
}
