package com.mr_toad.gpu_booster.api;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.config.DSAMode;
import com.mr_toad.gpu_booster.client.rendering.gl.VertexBufferCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;

import org.jetbrains.annotations.Nullable;
import java.nio.ByteBuffer;

//Compat bridge for GL 4.5 with Minecraft and GL <=3
@Environment(EnvType.CLIENT)
public class GBGL {

    public static ThreadLocal<VertexFormat> CURRENT = ThreadLocal.withInitial(() -> null);

    public static void preGen() {
        VertexBufferCache.preGen();
    }

    public static int getVAO() {
        if (CURRENT.get() != null) {
            return VertexBufferCache.getVAO(CURRENT.get());
        } else {
            return createVAO();
        }
    }

    public static int createVAO() {
        return GL45.glCreateVertexArrays();
    }

    public static void addVAO2VBO(int vao, int binding, int vbo, long offset, int stride) {
        RenderSystem.assertOnRenderThread();
        GL45.glVertexArrayVertexBuffer(vao, binding, vbo, offset, stride);
    }

    public static void enableVAOAttrib(int vao, int index) {
        RenderSystem.assertOnRenderThread();
        GL45.glEnableVertexArrayAttrib(vao, index);
    }

    public static void vaoFormat(int vao, int attrib, int size, int type, boolean norm, int relativeOffset) {
        RenderSystem.assertOnRenderThread();
        GL45.glVertexArrayAttribFormat(vao, attrib, size, type, norm, relativeOffset);
    }

    public static void vaoFormat(int vao, int attrib, int size, int type, int relativeOffset) {
        RenderSystem.assertOnRenderThread();
        GL45.glVertexArrayAttribIFormat(vao, attrib, size, type, relativeOffset);
    }

    public static void bindVAOAttrib(int vao, int index, int bindIndex) {
        RenderSystem.assertOnRenderThread();
        GL45.glVertexArrayAttribBinding(vao, index, bindIndex);
    }

    public static int createVBO() {
        return GL45.glCreateBuffers();
    }

    public static void namedBufferData(int id, @Nullable ByteBuffer buffer, int usage) {
        RenderSystem.assertOnRenderThread();
        if (buffer == null) {
            GL45.glNamedBufferData(id, MemoryUtil.NULL, usage);
        } else {
            GL45.glNamedBufferData(id, buffer, usage);
        }
    }

    public static void addEBO2VAO(int vao, int ebo) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL45.glVertexArrayElementBuffer(vao, ebo);
    }

    public static int makeFBO() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
            return GL45.glCreateFramebuffers();
        } else {
            return GlStateManager.glGenFramebuffers();
        }
    }

    public static void namedFramebufferTexture(int fbo, int attachment, int texture) {
        namedFramebufferTexture(fbo, attachment, texture, 0);
    }

    public static void namedFramebufferTexture(int fbo, int attachment, int texture, int level) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL45.glNamedFramebufferTexture(fbo, attachment, texture, level);
    }

    public static void unbindFramebuffer() {
        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
    }

    public static int make2DTexture() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
            return GL45.glCreateTextures(GL11.GL_TEXTURE_2D);
        } else {
            return TextureUtil.generateTextureId();
        }
    }

    public static void textureParameter(int texture, int name, int param) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL45.glTextureParameteri(texture, name, param);
    }

    public static void textureStorage(int texture, int format, int w, int h) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL45.glTextureStorage2D(texture, 1, format, w, h);
    }

    public static int createRBO() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL45.glCreateRenderbuffers();
    }

    public static int genRBO() {
        RenderSystem.assertOnRenderThreadOrInit();
        return GL30.glGenRenderbuffers();
    }

    public static void bindRBO(int id) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL45.glBindRenderbuffer(GL45.GL_RENDERBUFFER, id);
    }

    public static void storageRBO(int w, int h) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL45.glNamedRenderbufferStorage(GL45.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, w, h);
    }

    public static void storageNamedRBO(int rbo, int w, int h) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL45.glNamedRenderbufferStorage(rbo, GL14.GL_DEPTH_COMPONENT24, w, h);
    }

    public static void framebufferRBO(int rbo) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL45.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, rbo);
    }

    public static void framebufferNamedRBO(int fbo, int rbo) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL45.glNamedFramebufferRenderbuffer(fbo, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, rbo);
    }

    public static void deleteRBO(int rbo) {
        RenderSystem.assertOnRenderThreadOrInit();
        GL30.glDeleteRenderbuffers(rbo);
    }
}
