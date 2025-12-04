package com.mr_toad.gpu_booster.client.mixin.random;

import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.rendering.math.TableGaussianGenerator;
import net.minecraft.util.math.random.GaussianGenerator;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandomImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Xoroshiro128PlusPlusRandom.class)
public abstract class Xoroshiro128PlusPlusRandomMixin {

    @Mutable @Shadow @Final private GaussianGenerator gaussianGenerator;

    @Inject(method = "<init>(J)V", at = @At("TAIL"))
    public void modifyGaussian(long seed, CallbackInfo ci) {
        this.gb$modgaus();
    }

    @Inject(method = "<init>(Lnet/minecraft/util/math/random/RandomSeed$XoroshiroSeed;)V", at = @At("TAIL"))
    public void modifyGaussian(RandomSeed.XoroshiroSeed seed, CallbackInfo ci) {
        this.gb$modgaus();
    }

    @Inject(method = "<init>(JJ)V", at = @At("TAIL"))
    public void modifyGaussian(long seedLo, long seedHi, CallbackInfo ci) {
        this.gb$modgaus();
    }

    @Inject(method = "<init>(Lnet/minecraft/util/math/random/Xoroshiro128PlusPlusRandomImpl;)V", at = @At("TAIL"))
    public void modifyGaussian(Xoroshiro128PlusPlusRandomImpl implementation, CallbackInfo ci) {
        this.gb$modgaus();
    }

    @Unique
    private void gb$modgaus() {
        if (GPUBooster.CONFIG.fastRandom.get()) {
            this.gaussianGenerator = new TableGaussianGenerator((Random) this);
        }
    }
}
