package com.mr_toad.gpu_booster.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.rendering.math.GBFMatrix4f;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.Window;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.jetbrains.annotations.Nullable;

@Mixin(ShaderProgram.class)
public abstract class ShaderProgramMixin {

    @Shadow @Final @Nullable public GlUniform modelViewMat;
    @Shadow @Final @Nullable public GlUniform projectionMat;
    @Shadow @Final @Nullable public GlUniform colorModulator;
    @Shadow @Final @Nullable public GlUniform glintAlpha;
    @Shadow @Final @Nullable public GlUniform fogStart;
    @Shadow @Final @Nullable public GlUniform fogEnd;
    @Shadow @Final @Nullable public GlUniform fogColor;
    @Shadow @Final @Nullable public GlUniform fogShape;
    @Shadow @Final @Nullable public GlUniform textureMat;
    @Shadow @Final @Nullable public GlUniform gameTime;
    @Shadow @Final @Nullable public GlUniform screenSize;
    @Shadow @Final @Nullable public GlUniform lineWidth;

    @Shadow public abstract void addSampler(String name, Object sampler);

    @Inject(method = "initializeUniforms", at = @At(value = "HEAD"), cancellable = true)
    public void reinitUniforms(VertexFormat.DrawMode drawMode, Matrix4f viewMatrix, Matrix4f projectionMatrix, Window window, CallbackInfo ci) {
        if (GPUBooster.CONFIG.fastMath.get()) {
            for(int i = 0; i < 12; ++i) {
                int j = RenderSystem.getShaderTexture(i);
                this.addSampler("Sampler" + i, j);
            }

            this.gb$baseUniformsSetup(drawMode, viewMatrix, projectionMatrix, window);
            ci.cancel();
        }
    }

    @Unique
    private void gb$baseUniformsSetup(VertexFormat.DrawMode drawMode, Matrix4f viewMatrix, Matrix4f projectionMatrix, Window window) {
        if (this.modelViewMat != null) {
            this.modelViewMat.set(new GBFMatrix4f(viewMatrix));
        }

        if (this.projectionMat != null) {
            this.projectionMat.set(new GBFMatrix4f(projectionMatrix));
        }

        if (this.colorModulator != null) {
            this.colorModulator.set(RenderSystem.getShaderColor());
        }

        if (this.glintAlpha != null) {
            this.glintAlpha.set(RenderSystem.getShaderGlintAlpha());
        }

        if (this.fogStart != null) {
            this.fogStart.set(RenderSystem.getShaderFogStart());
        }

        if (this.fogEnd != null) {
            this.fogEnd.set(RenderSystem.getShaderFogEnd());
        }

        if (this.fogColor != null) {
            this.fogColor.set(RenderSystem.getShaderFogColor());
        }

        if (this.fogShape != null) {
            this.fogShape.set(RenderSystem.getShaderFogShape().getId());
        }

        if (this.textureMat != null) {
            this.textureMat.set(new GBFMatrix4f(RenderSystem.getTextureMatrix()));
        }

        if (this.gameTime != null) {
            this.gameTime.set(RenderSystem.getShaderGameTime());
        }

        if (this.screenSize != null) {
            this.screenSize.set((float) window.getFramebufferWidth(), (float) window.getFramebufferHeight());
        }

        if (this.lineWidth != null && (drawMode == VertexFormat.DrawMode.LINES || drawMode == VertexFormat.DrawMode.LINE_STRIP)) {
            this.lineWidth.set(RenderSystem.getShaderLineWidth());
        }

        RenderSystem.setupShaderLights((ShaderProgram) (Object) (this));
    }
}
