package com.mr_toad.gpu_booster.client.mixin;

import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.config.DSAMode;
import com.mr_toad.gpu_booster.client.rendering.gl.GBGL;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.BufferAllocator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void setupCtx(BufferAllocator allocator, VertexFormat.DrawMode drawMode, VertexFormat format, CallbackInfo ci) {
        if (GPUBooster.CONFIG.hasDSA(DSAMode.VBO)) {
            GBGL.CURRENT.set(format);
        }
    }
}
