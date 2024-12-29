package com.mr_toad.gpu_tape.client.mixin;

import com.mr_toad.gpu_tape.client.GpuTape;
import com.mr_toad.gpu_tape.client.util.CleanException;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.ref.Cleaner;

@Mixin(Framebuffer.class)
public abstract class FramebufferMixin implements FramebufferFixer, Cleaner.Cleanable {

    @Shadow protected int colorAttachment;
    @Shadow protected int depthAttachment;
    @Shadow public int fbo;

    @Override
    public void clean() {
        try {
            if (!GpuTape.isVulkan() && (this.colorAttachment > -1 || this.depthAttachment > -1 || this.fbo > -1)) {
                GpuTape.FIXERS.add(this);
            }
        } catch (Exception e) {
            throw new CleanException("Failed to finalize/clean '" + this + "'", e);
        }
    }

    @Override
    public void destroy() {
        GlStateManager._bindTexture(0);
        GlStateManager._glBindFramebuffer(36160, 0);
    }

    @Override
    public void release() {
        if (this.colorAttachment > -1) {
            TextureUtil.releaseTextureId(this.colorAttachment);
        }

        if (this.depthAttachment > -1) {
            TextureUtil.releaseTextureId(this.depthAttachment);
        }

        if (this.fbo > -1) {
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
            GlStateManager._glDeleteFramebuffers(this.fbo);
        }
    }

    @Override
    public String toString() {
        return "FrB(" + this.colorAttachment + ", " + this.depthAttachment + ", " + this.fbo + ")";
    }

    @Override
    public int hashCode() {
        return MathHelper.idealHash(this.colorAttachment + this.depthAttachment + this.fbo);
    }
}
