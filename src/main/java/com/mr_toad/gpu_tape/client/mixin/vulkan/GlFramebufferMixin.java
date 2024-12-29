package com.mr_toad.gpu_tape.client.mixin.vulkan;

import com.mr_toad.gpu_tape.client.GpuTape;
import com.mr_toad.gpu_tape.client.util.CleanException;
import com.mr_toad.gpu_tape.client.util.FramebufferFixer;
import net.vulkanmod.gl.GlFramebuffer;
import net.vulkanmod.vulkan.framebuffer.Framebuffer;
import net.vulkanmod.vulkan.framebuffer.RenderPass;
import net.vulkanmod.vulkan.texture.VulkanImage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.ref.Cleaner;

@Mixin(value = GlFramebuffer.class, remap = false)
public abstract class GlFramebufferMixin implements FramebufferFixer, Cleaner.Cleanable {

    @Shadow @Final private int id;

    @Shadow Framebuffer framebuffer;
    @Shadow RenderPass renderPass;
    @Shadow VulkanImage colorAttachment;
    @Shadow VulkanImage depthAttachment;

    @Shadow private static GlFramebuffer boundFramebuffer;

    @Override
    public void clean() {
        try {
            if (GpuTape.isVulkan() && (this.id != -1 || this.colorAttachment != null || this.depthAttachment != null)) {
                GpuTape.FIXERS.add(this);
            }
        } catch (Exception e) {
            throw new CleanException("Failed to clean vulkan framebuffer'" + this + "'", e);
        }
    }

    @Override
    public void destroy() {
        this.colorAttachment.doFree();
        this.depthAttachment.doFree();
        if (boundFramebuffer != null) {
            boundFramebuffer.getFramebuffer().cleanUp();
            boundFramebuffer.getRenderPass().cleanUp();
        }
        GlFramebuffer.resetBoundFramebuffer();
    }

    @Override
    public void release() {
        this.framebuffer.cleanUp(true);
        this.renderPass.cleanUp();
    }

    @Override
    public String toString() {
        return "VFramebuffer: " + this.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
