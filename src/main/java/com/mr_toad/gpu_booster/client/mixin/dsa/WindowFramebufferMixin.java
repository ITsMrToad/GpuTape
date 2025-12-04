package com.mr_toad.gpu_booster.client.mixin.dsa;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mr_toad.gpu_booster.api.GBGL;
import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.config.DSAMode;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.WindowFramebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WindowFramebuffer.class)
public abstract class WindowFramebufferMixin extends Framebuffer {

    @Shadow protected abstract WindowFramebuffer.Size findSuitableSize(int width, int height);

    public WindowFramebufferMixin(boolean useDepth) {
        super(useDepth);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void init(int width, int height, CallbackInfo ci) {
        WindowFramebuffer.Size size = this.findSuitableSize(width, height);
        this.fbo = GBGL.makeFBO();
        if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
            GBGL.textureParameter(this.colorAttachment, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GBGL.textureParameter(this.colorAttachment, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GBGL.textureParameter(this.colorAttachment, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GBGL.textureParameter(this.colorAttachment, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GBGL.namedFramebufferTexture(this.fbo, GlConst.GL_COLOR_ATTACHMENT0, this.colorAttachment);
            if (GPUBooster.CONFIG.canCreateRenderbuffer()) {
                GBGL.framebufferNamedRBO(this.fbo, this.depthAttachment);
            } else {
                GBGL.textureParameter(this.depthAttachment, GL14.GL_TEXTURE_COMPARE_MODE, 0);
                GBGL.textureParameter(this.depthAttachment, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GBGL.textureParameter(this.depthAttachment, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GBGL.textureParameter(this.depthAttachment, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GBGL.textureParameter(this.depthAttachment, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                GBGL.namedFramebufferTexture(this.fbo, GlConst.GL_DEPTH_ATTACHMENT, this.depthAttachment);
            }
        } else {
            GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
            GlStateManager._bindTexture(this.colorAttachment);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.colorAttachment, 0);
            if (GPUBooster.CONFIG.canCreateRenderbuffer()) {
                GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
                GBGL.framebufferRBO(this.depthAttachment);
            } else {
                GlStateManager._bindTexture(this.depthAttachment);
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, 0);
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
                GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_DEPTH_ATTACHMENT, GlConst.GL_TEXTURE_2D, this.depthAttachment, 0);
                GlStateManager._bindTexture(0);
            }
        }
        this.viewportWidth = size.width;
        this.viewportHeight = size.height;
        this.textureWidth = size.width;
        this.textureHeight = size.height;
        this.checkFramebufferStatus();
        if (!GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
            GBGL.unbindFramebuffer();
        }
        ci.cancel();
    }

    @Redirect(method = "findSuitableSize", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/TextureUtil;generateTextureId()I", ordinal = 0))
    public int genGPUBColor() {
        return GBGL.make2DTexture();
    }

    @Redirect(method = "findSuitableSize", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/TextureUtil;generateTextureId()I", ordinal = 1))
    public int genGPUBDepth() {
        if (GPUBooster.CONFIG.canCreateRenderbuffer()) {
            return GPUBooster.CONFIG.hasDSA(DSAMode.FBO) ? GBGL.createRBO() : GBGL.genRBO();
        } else {
            return GBGL.make2DTexture();
        }
    }

    @Inject(method = "supportsColor", at = @At("HEAD"), cancellable = true)
    private void supportsColorNamed(WindowFramebuffer.Size size, CallbackInfoReturnable<Boolean> cir) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._getError();
        if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
            GBGL.textureStorage(this.colorAttachment, GL11.GL_RGBA8, size.width, size.height);
        } else {
            GlStateManager._bindTexture(this.colorAttachment);
            GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, size.width, size.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null);
        }
        cir.setReturnValue(GlStateManager._getError() != GL11.GL_OUT_OF_MEMORY);
    }

    @Inject(method = "supportsDepth", at = @At("HEAD"), cancellable = true)
    private void supportsDepthNamed(WindowFramebuffer.Size size, CallbackInfoReturnable<Boolean> cir) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._getError();
        if (GPUBooster.CONFIG.canCreateRenderbuffer()) {
            if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                GBGL.storageNamedRBO(this.depthAttachment, size.width, size.height);
            } else {
                GBGL.bindRBO(this.depthAttachment);
                GBGL.storageRBO(size.width, size.height);
            }
        } else {
            if (GPUBooster.CONFIG.hasDSA(DSAMode.FBO)) {
                GBGL.textureStorage(this.colorAttachment, GL11.GL_DEPTH_COMPONENT, size.width, size.height);
            } else {
                GlStateManager._bindTexture(this.depthAttachment);
                GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, size.width, size.height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, null);
            }
        }
        cir.setReturnValue(GlStateManager._getError() != GL11.GL_OUT_OF_MEMORY);
    }
}
