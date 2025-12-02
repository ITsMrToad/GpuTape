package com.mr_toad.gpu_booster.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.rendering.math.GBFMatrix4f;
import net.minecraft.client.render.BufferRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BufferRenderer.class)
public abstract class BufferRendererMixin {

    @Redirect(method = "drawWithGlobalProgramInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getModelViewMatrix()Lorg/joml/Matrix4f;"))
    private static Matrix4f getFastModelViewMat() {
        if (!GPUBooster.CONFIG.fastMath.get()) {
            return RenderSystem.getModelViewMatrix();
        }
        return new GBFMatrix4f(RenderSystem.getModelViewMatrix());
    }

    @Redirect(method = "drawWithGlobalProgramInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;getProjectionMatrix()Lorg/joml/Matrix4f;"))
    private static Matrix4f getFastProjMat() {
        if (!GPUBooster.CONFIG.fastMath.get()) {
            return RenderSystem.getProjectionMatrix();
        }
        return new GBFMatrix4f(RenderSystem.getProjectionMatrix());
    }
}
