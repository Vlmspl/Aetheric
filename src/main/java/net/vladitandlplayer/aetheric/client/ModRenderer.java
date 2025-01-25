package net.vladitandlplayer.aetheric.client;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.FramebufferManager;
import foundry.veil.api.client.render.vertex.VertexArray;
import foundry.veil.api.client.render.vertex.VertexArrayBuilder;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.Window;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.vladitandlplayer.aetheric.Aetheric;
import org.joml.*;

import java.io.IOException;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static net.minecraft.client.session.telemetry.TelemetryEventProperty.RENDER_DISTANCE;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

@Environment(EnvType.CLIENT)
public class ModRenderer {
    private static final Identifier HALLUCINATION_FBO = Aetheric.path("hallucination");

    private static final Matrix4f RENDER_MODEL_VIEW = new Matrix4f();
    private static final Matrix4f RENDER_PROJECTION = new Matrix4f();
    private static final Quaternionf VIEW = new Quaternionf();



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



        Quaternionf cameraRot = camera.getRotation();
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.translate((float) -cameraX, (float) -cameraY, (float) -cameraZ); // Translate to camera position
        viewMatrix.rotate(cameraRot); // Apply camera rotation

        float[] vertices = new float[] {
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                0.0f, 1.0f, 0.0f
        };
        int vertexCount = 3; // Number of vertices (for a triangle, 3 vertices)
        int vertexSize = 3 * Float.BYTES; // 3 floats per vertex (x, y, z)


        int[] indexes = new int[] {
                0, 1, 2
        };
        int indexCount = 3;
        int indexSize = Integer.BYTES;


        VertexFormat vertexFormat = VertexFormat.builder().add("position", VertexFormatElement.POSITION).build();



        int bufferSize = vertexCount * vertexSize;
        int indexBufferSize = indexCount * indexSize;
        BufferAllocator bufferAllocator = new BufferAllocator(bufferSize);


        BufferAllocator.CloseableBuffer buffer = bufferAllocator.getAllocated();



        BuiltBuffer.DrawParameters drawParameters = new BuiltBuffer.DrawParameters(vertexFormat, 0, bufferSize, VertexFormat.DrawMode.TRIANGLES, VertexFormat.IndexType.INT);

        fbo.bind(true);
        VertexArray vertexArray = VertexArray.create();
        vertexArray.bind();
        vertexArray.upload(new BuiltBuffer(buffer, drawParameters), VertexArray.DrawUsage.DYNAMIC);
        vertexArray.uploadIndexBuffer(drawParameters);


        vertexArray.draw(GL_STATIC_DRAW);

        VertexArray.unbind();
        AdvancedFbo.unbind();
    }



}
