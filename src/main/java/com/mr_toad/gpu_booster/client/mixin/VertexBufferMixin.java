package com.mr_toad.gpu_booster.client.mixin;

import com.google.common.annotations.Beta;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.config.DSAMode;
import com.mr_toad.gpu_booster.client.rendering.gl.GBGL;
import com.mr_toad.gpu_booster.client.rendering.gl.VertexFormatCache;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import org.lwjgl.opengl.GL15;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.jetbrains.annotations.Nullable;
import java.nio.ByteBuffer;

@Beta
@Mixin(VertexBuffer.class)
public abstract class VertexBufferMixin {

    @Shadow private int indexBufferId;
    @Shadow private int vertexBufferId;
    @Shadow private int vertexArrayId;
    @Shadow @Final private VertexBuffer.Usage usage;
    @Shadow @Nullable private RenderSystem.ShapeIndexBuffer sharedSequentialIndexBuffer;

    @Shadow @Nullable protected abstract RenderSystem.ShapeIndexBuffer uploadIndexBuffer(BuiltBuffer.DrawParameters parameters, @Nullable ByteBuffer indexBuffer);
    @Shadow protected abstract VertexFormat uploadVertexBuffer(BuiltBuffer.DrawParameters parameters, @Nullable ByteBuffer vertexBuffer);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void reinitNamed(VertexBuffer.Usage usage, CallbackInfo ci) {
        if (GPUBooster.CONFIG.hasDSA(DSAMode.VBO)) {
            RenderSystem.assertOnRenderThread();
            this.vertexBufferId = VertexFormatCache.getVBO();
            this.indexBufferId = VertexFormatCache.getEBO();
            this.vertexArrayId = GBGL.getVAO();
        }
    }

    @Redirect(method = "upload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/VertexBuffer;uploadVertexBuffer(Lnet/minecraft/client/render/BuiltBuffer$DrawParameters;Ljava/nio/ByteBuffer;)Lnet/minecraft/client/render/VertexFormat;"))
    public VertexFormat uploadVBO(VertexBuffer instance, BuiltBuffer.DrawParameters parameters, ByteBuffer indexBuffer) {
        if (GPUBooster.CONFIG.hasDSA(DSAMode.VBO)) {
            return this.uploadNamedVBO(parameters, indexBuffer);
        } else {
            return this.uploadVertexBuffer(parameters, indexBuffer);
        }
    }

    @Redirect(method = "upload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/VertexBuffer;uploadIndexBuffer(Lnet/minecraft/client/render/BuiltBuffer$DrawParameters;Ljava/nio/ByteBuffer;)Lcom/mojang/blaze3d/systems/RenderSystem$ShapeIndexBuffer;"))
    public RenderSystem.ShapeIndexBuffer uploadEBO(VertexBuffer instance, BuiltBuffer.DrawParameters parameters, ByteBuffer indexBuffer) {
        if (GPUBooster.CONFIG.hasDSA(DSAMode.VBO)) {
            return this.uploadNamedEBO(parameters, indexBuffer);
        } else {
            return this.uploadIndexBuffer(parameters, indexBuffer);
        }
    }

    @Inject(method = "close", at = @At("HEAD"), cancellable = true)
    public void closeNamed(CallbackInfo ci) {
        if (GPUBooster.CONFIG.hasDSA(DSAMode.VBO)) {
            VertexFormatCache.clean(this.vertexArrayId, this.vertexBufferId, this.indexBufferId, this.usage);
            this.vertexBufferId = -1;
            this.indexBufferId = -1;
            this.vertexArrayId = -1;
            ci.cancel();
        }
    }

    @Unique
    private VertexFormat uploadNamedVBO(BuiltBuffer.DrawParameters parameters, @Nullable ByteBuffer vertexBuffer) {
        if (vertexBuffer != null) {
            GBGL.namedBufferData(this.vertexBufferId, vertexBuffer, this.usage == VertexBuffer.Usage.DYNAMIC ? GL15.GL_DYNAMIC_DRAW : GL15.GL_STATIC_DRAW);
        }
        GBGL.addVAO2VBO(this.vertexArrayId, 0, this.vertexBufferId, 0L, parameters.format().getVertexSizeByte());
        return parameters.format();
    }

    @Unique
    @Nullable
    private RenderSystem.ShapeIndexBuffer uploadNamedEBO(BuiltBuffer.DrawParameters parameters, @Nullable ByteBuffer indexBuffer) {
        if (indexBuffer != null) {
            GBGL.namedBufferData(this.indexBufferId, indexBuffer, this.usage == VertexBuffer.Usage.DYNAMIC ? GL15.GL_DYNAMIC_DRAW : GL15.GL_STATIC_DRAW);
            GBGL.addEBO2VAO(this.vertexArrayId, this.indexBufferId);
            return null;
        } else {
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(parameters.mode());
            if (shapeIndexBuffer != this.sharedSequentialIndexBuffer || !shapeIndexBuffer.isLargeEnough(parameters.indexCount())) {
                shapeIndexBuffer.bindAndGrow(parameters.indexCount());
            }
            return shapeIndexBuffer;
        }
    }
}
