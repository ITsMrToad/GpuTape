package com.mr_toad.gpu_booster.client.mixin.framebuffer;

import com.google.common.annotations.Beta;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.config.DSAMode;
import com.mr_toad.gpu_booster.client.rendering.gl.GBGL;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.IntBuffer;

@Beta
@Mixin(Framebuffer.class)
public abstract class FramebufferMixin {

    @Shadow public int fbo;
    @Shadow public int viewportWidth;
    @Shadow public int viewportHeight;
    @Shadow public int textureWidth;
    @Shadow public int textureHeight;
    @Shadow protected int depthAttachment;
    @Shadow protected int colorAttachment;
    @Shadow public int texFilter;
    @Shadow @Final public boolean useDepthAttachment;

    @Shadow protected abstract void setTexFilter(int texFilter, boolean force);
    @Shadow public abstract void checkFramebufferStatus();
    @Shadow public abstract void clear(boolean getError);
    @Shadow public abstract void endRead();
    @Shadow public abstract void endWrite();

    @Inject(method = "initFbo", at = @At("HEAD"), cancellable = true)
    public void initNamed(int width, int height, boolean getError, CallbackInfo ci) {
        RenderSystem.assertOnRenderThreadOrInit();
        int i = RenderSystem.maxSupportedTextureSize();
        if (width > 0 && width <= i && height > 0 && height <= i) {
            this.viewportWidth = width;
            this.viewportHeight = height;
            this.textureWidth = width;
            this.textureHeight = height;
            this.fbo = GPUBooster.CONFIG.hasDSA(DSAMode.FBO) ? GBGL.createFBO() : GlStateManager.glGenFramebuffers();
            if (this.useDepthAttachment) {
                if (GPUBooster.CONFIG.canCreateRenderbuffer()) {
                    if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                        this.depthAttachment = GBGL.createRBO();
                        GBGL.storageNamedRBO(this.depthAttachment, width, height);
                    } else {
                        this.depthAttachment = GBGL.genRBO();
                        GBGL.bindRBO(this.depthAttachment);
                        GBGL.storageRBO(width, height);
                    }
                } else {
                    if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                        this.depthAttachment = GL45.glCreateTextures(GL11.GL_TEXTURE_2D);
                        GL45.glTextureParameteri(this.depthAttachment, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                        GL45.glTextureParameteri(this.depthAttachment, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GL45.glTextureParameteri(this.depthAttachment, GL14.GL_TEXTURE_COMPARE_MODE, 0);
                        GL45.glTextureParameteri(this.depthAttachment, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                        GL45.glTextureParameteri(this.depthAttachment, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                        GL45.glTextureStorage2D(this.depthAttachment, 1, GL14.GL_DEPTH_COMPONENT24, this.textureWidth, this.textureHeight);
                    } else {
                        this.depthAttachment = TextureUtil.generateTextureId();
                        GlStateManager._bindTexture(this.depthAttachment);
                        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, 0);
                        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                        GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                        GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, this.textureWidth, this.textureHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, null);
                    }
                }
            }

            if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                this.colorAttachment = GL45.glCreateTextures(GL11.GL_TEXTURE_2D);
                this.setTexFilter(GlConst.GL_NEAREST, true);
                GL45.glTextureParameteri(this.colorAttachment, GlConst.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GL45.glTextureParameteri(this.colorAttachment, GlConst.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                GL45.glTextureStorage2D(this.colorAttachment, 1, GL11.GL_RGBA8, this.textureWidth, this.textureHeight);
                GBGL.namedFramebufferTexture(this.fbo, GlConst.GL_COLOR_ATTACHMENT0, this.colorAttachment, 0);
            } else {
                this.colorAttachment = TextureUtil.generateTextureId();
                this.setTexFilter(GlConst.GL_NEAREST, true);
                GlStateManager._bindTexture(this.colorAttachment);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.textureWidth, this.textureHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null);
                GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
                GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.colorAttachment, 0);
            }

            if (this.useDepthAttachment) {
                if (GPUBooster.CONFIG.canCreateRenderbuffer()) {
                    if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                        GBGL.framebufferNamedRBO(this.fbo, this.depthAttachment);
                    } else {
                        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
                        GBGL.framebufferRBO(this.depthAttachment);
                    }
                } else {
                    if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                        GBGL.namedFramebufferTexture(this.fbo, GlConst.GL_DEPTH_ATTACHMENT, this.depthAttachment);
                    } else {
                        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
                        GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GlConst.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, this.depthAttachment, 0);
                    }
                }
            }
            this.checkFramebufferStatus();
            this.clear(getError);
            if (!GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                this.endRead();
            }
        } else {
            throw new IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + i + ")");
        }
        ci.cancel();
    }

    @Inject(method = "setTexFilter(IZ)V", at = @At("HEAD"), cancellable = true)
    public void setTexFil(int texFilter, boolean force, CallbackInfo ci) {
        RenderSystem.assertOnRenderThreadOrInit();
        if (force || texFilter != this.texFilter) {
            this.texFilter = texFilter;
            if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                GL45.glTextureParameteri(this.colorAttachment, GL11.GL_TEXTURE_MIN_FILTER, texFilter);
                GL45.glTextureParameteri(this.colorAttachment, GL11.GL_TEXTURE_MAG_FILTER, texFilter);
            } else {
                GlStateManager._bindTexture(this.colorAttachment);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, texFilter);
                GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, texFilter);
                GlStateManager._bindTexture(0);
            }
        }
        ci.cancel();
    }

    @Inject(method = "delete", at = @At(value = "HEAD"), cancellable = true)
    public void deleteRBON(CallbackInfo ci) {
        RenderSystem.assertOnRenderThreadOrInit();
        if (!GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
            this.endRead();
            this.endWrite();
        }
        if (this.depthAttachment > -1) {
            if (GPUBooster.CONFIG.canCreateRenderbuffer()) {
                GBGL.deleteRBO(this.depthAttachment);
            } else {
                if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                    GL45.glDeleteTextures(this.depthAttachment);
                } else {
                    TextureUtil.releaseTextureId(this.depthAttachment);
                }
            }
            this.depthAttachment = -1;
        }

        if (this.colorAttachment > -1) {
            if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                GL45.glDeleteTextures(this.colorAttachment);
            } else {
                TextureUtil.releaseTextureId(this.colorAttachment);
            }
            this.colorAttachment = -1;
        }

        if (this.fbo > -1) {
            if (!GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
            }
            GlStateManager._glDeleteFramebuffers(this.fbo);
            this.fbo = -1;
        }
        ci.cancel();
    }
}
