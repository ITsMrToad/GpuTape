package com.mr_toad.gpu_booster.client.config;

import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.lib.api.config.ToadConfig;
import com.mr_toad.lib.api.config.entry.BoolEntry;
import com.mr_toad.lib.api.config.entry.CommonEntries;
import com.mr_toad.lib.api.config.entry.EnumEntry;
import com.mr_toad.lib.api.config.entry.primitive.ShortEntry;
import com.mr_toad.lib.api.config.util.DeprecationRule;
import com.mr_toad.lib.api.config.util.HighlightWarning;
import com.mr_toad.lib.api.config.util.PerformanceImpact;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GBConfig extends ToadConfig {

    private static final DeprecationRule RBO_UNSUPPORTED = new DeprecationRule(() -> !GPUBooster.BUFFER_STORAGE).addTooltip(Text.translatable("gb.config.rbo_depth.deprecated.unsupported"));
    private static final DeprecationRule RBO_IRIS = new DeprecationRule(() -> FabricLoader.getInstance().isModLoaded("iris")).addTooltip(Text.translatable("gb.config.rbo_depth.deprecated.iris"));
    private static final DeprecationRule DSA_UNSUPPORTED = new DeprecationRule(() -> !GPUBooster.DSA).addTooltip(Text.translatable("gb.config.dsa.deprecated.unsupported"));
    private static final DeprecationRule DSA_WORLD_LOADED = new DeprecationRule(() -> MinecraftClient.getInstance().world != null).addTooltip(Text.translatable("gb.config.dsa.deprecated.world_loaded"));
    private static final DeprecationRule RCP_SIZE = new DeprecationRule(() -> GPUBooster.CONFIG.dsa.get() == DSAMode.OFF).addTooltip(Text.translatable("gb.config.render_cycle_pool.deprecated"));

    public final BoolEntry renderbufferDepth;
    public final BoolEntry fastMath;
    public final BoolEntry fastRandom;
    public final EnumEntry<DSAMode> dsa;
    public final BoolEntry vertexFormatCache;
    public final ShortEntry renderCyclePoolSize;

    public GBConfig() {
        super(() -> "gpu_booster_config");
        this.renderbufferDepth = this.register(new BoolEntry("renderbuffer_depth", true).addTitle(Text.translatable("gb.config.rbo_depth")).withPerformanceImpact(PerformanceImpact.LOW_INCREASE).addDeprecationRule(RBO_UNSUPPORTED, RBO_IRIS));
        this.fastMath = this.register(new BoolEntry("fast_math", true).addTitle(Text.translatable("gb.config.fastmath")).withPerformanceImpact(PerformanceImpact.HIGH_INCREASE));
        this.fastRandom = this.register(new BoolEntry("fast_random", true).addTitle(Text.translatable("gb.config.fastrand")).withPerformanceImpact(PerformanceImpact.MEDIUM_INCREASE).withWarning(HighlightWarning.WORLD_RELOAD));
        this.dsa = this.register(CommonEntries.createEnum("dsa", DSAMode.ALL, DSAMode.values(), DSAMode::symbol, DSAMode.CODEC).addTitle(Text.translatable("gb.config.dsa")).withPerformanceImpact(PerformanceImpact.MEDIUM_INCREASE).addDeprecationRule(DSA_UNSUPPORTED, DSA_WORLD_LOADED));
        this.vertexFormatCache = this.register(new BoolEntry("vertex_format_cache", false).addTitle(Text.translatable("gb.config.vfc")).addDescription(Text.translatable("gb.config.vfc.tooltip")).withPerformanceImpact(PerformanceImpact.HIGH_INCREASE).withWarning(HighlightWarning.GAME_RELOAD).addDeprecationRule(DSA_UNSUPPORTED, RCP_SIZE));
        this.renderCyclePoolSize = this.register(new ShortEntry("render_cycle_pool_size", (short) 256).range((short) 128, (short) 432).setStep(16).addTitle(Text.translatable("gb.config.render_cycle_pool")).withPerformanceImpact(PerformanceImpact.MEDIUM_INCREASE).withWarning(HighlightWarning.GAME_RELOAD).addDeprecationRule(DSA_UNSUPPORTED, RCP_SIZE));
    }

    @Override
    public Text title() {
        return Text.literal("GPU Booster");
    }

    public boolean canCreateRenderbuffer() {
        return this.renderbufferDepth.get() && !FabricLoader.getInstance().isModLoaded("iris") && GPUBooster.GL45;
    }

    public boolean hasDSA(DSAMode target) {
        return MinecraftClient.getInstance().options.getGraphicsMode().getValue().getId() < GraphicsMode.FABULOUS.getId() && (this.dsa.get() == DSAMode.ALL || this.dsa.get() == target);
    }
}

