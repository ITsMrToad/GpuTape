package com.mr_toad.gpu_booster.client.mixin.math;

import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.rendering.math.GBFMatrix3f;
import com.mr_toad.gpu_booster.client.rendering.math.GBFMatrix4f;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MatrixStack.Entry.class)
public abstract class MatrixStackEntryMixin {

    @Mutable @Shadow @Final Matrix3f normalMatrix;
    @Mutable @Shadow @Final Matrix4f positionMatrix;

    @Inject(method = "<init>(Lorg/joml/Matrix4f;Lorg/joml/Matrix3f;)V", at = @At("TAIL"))
    public void initWithGBF(Matrix4f positionMatrix, Matrix3f normalMatrix, CallbackInfo ci) {
        if (GPUBooster.CONFIG.fastMath.get()) {
            this.normalMatrix = new GBFMatrix3f(normalMatrix);
            this.positionMatrix = new GBFMatrix4f(positionMatrix);
        }
    }
}
