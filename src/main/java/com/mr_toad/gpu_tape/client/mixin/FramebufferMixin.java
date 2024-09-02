package com.mr_toad.gpu_tape.client.mixin;

import com.mr_toad.gpu_tape.client.VideoTape;
import com.mr_toad.gpu_tape.client.error.CleanException;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.ref.Cleaner;

@Mixin(Framebuffer.class)
public abstract class FramebufferMixin implements Cleaner.Cleanable {

    @Shadow protected int colorAttachment;
    @Shadow protected int depthAttachment;
    @Shadow public int fbo;

    @Shadow @Final public boolean useDepthAttachment;

    @Override
    public void clean() {
        try {
            if (this.colorAttachment > -1 || this.depthAttachment > -1 || this.fbo > -1) {
                VideoTape.FRAMEBUFFERS.add(new Framebuffer(this.useDepthAttachment) {});
            }
        } catch (Exception e) {
            throw new CleanException("Failed to finalize/clean '" + this + "'", e);
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
