package net.vladitandlplayer.aetheric.client;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.FramebufferManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.vladitandlplayer.aetheric.Aetheric;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;

import static net.minecraft.client.session.telemetry.TelemetryEventProperty.RENDER_DISTANCE;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

@Environment(EnvType.CLIENT)
public class ModRenderer {
    private static final Identifier HALLUCINATION_FBO = Aetheric.path("hallucination");

    private static final Matrix4f RENDER_MODEL_VIEW = new Matrix4f();
    private static final Matrix4f RENDER_PROJECTION = new Matrix4f();
    private static final Quaternionf VIEW = new Quaternionf();

    private static final Long2ObjectMap<HallucinationTexture> TEXTURES = new Long2ObjectArrayMap<>();


    public static void renderHallucinations(WorldRenderer levelRenderer, VertexConsumerProvider.Immediate bufferSource,
                                            MatrixStack matrixStack, Matrix4fc frustumMatrix, Matrix4fc projectionMatrix, Camera camera) {
        if (VeilLevelPerspectiveRenderer.isRenderingPerspective() || !projectionMatrix.isFinite()) {
            return;
        }

        // Get camera position
        double cameraX = MinecraftClient.getInstance().getCameraEntity().getX();
        double cameraY = MinecraftClient.getInstance().getCameraEntity().getY();
        double cameraZ = MinecraftClient.getInstance().getCameraEntity().getZ();

        // Framebuffer setup
        FramebufferManager framebufferManager = VeilRenderSystem.renderer().getFramebufferManager();
        AdvancedFbo fbo = framebufferManager.getFramebuffer(HALLUCINATION_FBO);
        if (fbo == null) {
            return;
        }

        // Hallucination texture
        HallucinationTexture hallucinationTexture = TEXTURES.computeIfAbsent(0, unused -> new HallucinationTexture());

        // Calculate projection matrix
        Window window = MinecraftClient.getInstance().getWindow();
        float aspect = (float) window.getWidth() / window.getHeight();
        float fov = projectionMatrix.perspectiveFov();
        RENDER_PROJECTION.setPerspective(fov, aspect, 0.3F, RENDER_DISTANCE * 4);

        // Calculate view matrix
        Vector3f cameraDir = camera.();
        Vector3f cameraUp = camera.getUpVector();
        Matrix4f viewMatrix = new Matrix4f().identity().lookAlong(cameraDir, cameraUp);

        // Optional: Add offset or distortions for hallucination effects
        Vector3f hallucinationOffset = new Vector3f(
                (float) (cameraX + Math.random() * 0.5),
                (float) (cameraY + Math.random() * 0.5),
                (float) (cameraZ + Math.random() * 0.5)
        );

        // Render hallucinations with modified matrices and offset
        VeilLevelPerspectiveRenderer.render(fbo, RENDER_MODEL_VIEW, RENDER_PROJECTION, hallucinationOffset, viewMatrix, RENDER_DISTANCE, deltaTracker);
    }



    private static class HallucinationTexture extends AbstractTexture {
        private boolean rendered;

        private int width;
        private int height;

        private HallucinationTexture() {
            this.setFilter(false, true);
            this.width = -1;
            this.height = -1;
        }

        @Override
        public void load(ResourceManager manager) throws IOException {}

        public void copy(AdvancedFbo fbo) {
            int id = this.getGlId();
            int width = fbo.getWidth();
            int height = fbo.getHeight();
            if (this.width!=width || this.height!=height) {
                this.width = width;
                this.height = height;
                TextureUtil.prepareImage(NativeImage.InternalFormat.RGBA, id, 4, width, height);
            }

            RenderSystem.bindTexture(id);
            fbo.bindRead();
            glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);
            AdvancedFbo.unbind();
            glGenerateMipmap(GL_TEXTURE_2D);
        }

        public boolean hasRendered() {
            return this.rendered;
        }

        public void setRendered(boolean val) {
            this.rendered = val;
        }
    }
}
