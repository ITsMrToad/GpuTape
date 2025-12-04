package com.mr_toad.gpu_booster.client.mixin.random;

import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.rendering.math.TableGaussianGenerator;
import net.minecraft.util.math.random.GaussianGenerator;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.ThreadSafeRandom;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("deprecation")
@Mixin(ThreadSafeRandom.class)
public abstract class ThreadSafeRandomMixin {

    @Mutable @Shadow @Final private GaussianGenerator gaussianGenerator;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void modifyGaussian(long seed, CallbackInfo ci) {
        if (GPUBooster.CONFIG.fastRandom.get()) {
            this.gaussianGenerator = new TableGaussianGenerator((Random) this);
        }
    }
}
