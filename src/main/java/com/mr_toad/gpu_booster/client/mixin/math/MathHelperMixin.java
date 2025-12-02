package com.mr_toad.gpu_booster.client.mixin.math;

import com.mr_toad.gpu_booster.client.GPUBooster;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MathHelper.class)
public abstract class MathHelperMixin {

    @Inject(method = "floorMod(II)I", at = @At("HEAD"), cancellable = true)
    private static void fastFM(int dividend, int divisor, CallbackInfoReturnable<Integer> cir) {
        if (GPUBooster.CONFIG.fastMath.get()) {
            int m = dividend % divisor;
            if ((m ^ divisor) < 0) {
                m += divisor;
            }
            cir.setReturnValue(m);
        }
    }

    @Inject(method = "floorMod(FF)F", at = @At("HEAD"), cancellable = true)
    private static void fastFM(float dividend, float divisor, CallbackInfoReturnable<Float> cir) {
        if (GPUBooster.CONFIG.fastMath.get()) {
            cir.setReturnValue(dividend - MathHelper.floor(dividend / divisor) * divisor);
        }
    }

    @Inject(method = "floorMod(DD)D", at = @At("HEAD"), cancellable = true)
    private static void fastFM(double dividend, double divisor, CallbackInfoReturnable<Double> cir) {
        if (GPUBooster.CONFIG.fastMath.get()) {
            cir.setReturnValue(dividend - Math.floor(dividend / divisor) * divisor);
        }
    }

    @Inject(method = "ceilLog2", at = @At("HEAD"), cancellable = true)
    private static void fastCL2(int value, CallbackInfoReturnable<Integer> cir) {
        if (GPUBooster.CONFIG.fastMath.get()) {
            if (value <= 1) {
                cir.setReturnValue(0);
            }
            cir.setReturnValue(32 - Integer.numberOfLeadingZeros(value - 1));
        }
    }

    @Inject(method = "floorLog2", at = @At("HEAD"), cancellable = true)
    private static void fastFL2(int value, CallbackInfoReturnable<Integer> cir) {
        if (GPUBooster.CONFIG.fastMath.get()) {
            cir.setReturnValue(31 - Integer.numberOfLeadingZeros(value));
        }
    }
}
