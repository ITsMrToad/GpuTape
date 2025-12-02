package com.mr_toad.gpu_booster.client.mixin.math;

import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.rendering.math.GBFMath;
import com.mr_toad.lib.mtjava.math.MtMath;
import net.minecraft.util.math.GivensPair;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GivensPair.class)
public abstract class GivensPairMixin {

    @Unique private static final GivensPair IDENTITY = new GivensPair(0.0F, 1.0F);

    @Inject(method = "normalize", at = @At("HEAD"), cancellable = true)
    private static void fastNorm(float a, float b, CallbackInfoReturnable<GivensPair> cir) {
        if (GPUBooster.CONFIG.fastMath.get()) {
            float f = MtMath.lengthSquared(a, b);
            if (f < GBFMath.NORMALIZE_EPSILON) {
                cir.setReturnValue(IDENTITY);
            }
            float inv = GBFMath.invsqrt(f);
            cir.setReturnValue(new GivensPair(a * inv, b * inv));
        }
    }

    @Inject(method = "fromAngle", at = @At("HEAD"), cancellable = true)
    private static void fastAngle(float radians, CallbackInfoReturnable<GivensPair> cir) {
        if (GPUBooster.CONFIG.fastMath.get()) {
            float f = 0.5F * radians;
            float sin = MathHelper.sin(f);
            float cos = GBFMath.cosFromSin(sin, f);
            cir.setReturnValue(new GivensPair(sin, cos));
        }
    }
}
