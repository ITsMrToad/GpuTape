package com.mr_toad.gpu_booster.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mr_toad.gpu_booster.api.GBGL;
import com.mr_toad.gpu_booster.api.VertexFormatCacheAPI;
import com.mr_toad.gpu_booster.client.config.DSAMode;
import com.mr_toad.gpu_booster.client.config.GBConfig;
import com.mr_toad.gpu_booster.client.resource.UnregisteredIdsResourceSupplier;
import com.mr_toad.lib.api.config.ToadConfigs;
import com.mr_toad.lib.core.ToadLibClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceType;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GPUBooster implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("GPUBooster");
    public static final String MODID = "gpu_booster";

    public static final GBConfig CONFIG = new GBConfig();
    public static final UnregisteredIdsResourceSupplier UNREGISTERED_IDS = new UnregisteredIdsResourceSupplier();

    public static boolean GL45;
    public static boolean DSA;
    public static boolean BUFFER_STORAGE;

    @Override
    public void onInitializeClient() {
        RenderSystem.recordRenderCall(() -> {
            GLCapabilities caps = GL.getCapabilities();
            GL45 = caps.OpenGL45;
            DSA = caps.GL_ARB_direct_state_access || GL45;
            BUFFER_STORAGE = caps.GL_ARB_buffer_storage || GL45;
            if (CONFIG.hasDSA(DSAMode.VBO)) {
                GBGL.preGen();
            }
            LOGGER.info("GPUBooster caps initialized.");
        });
        ToadConfigs.create(MODID, CONFIG);
        System.setProperty("joml.fastmath", CONFIG.fastMath.get().toString());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(UNREGISTERED_IDS);
        if (CONFIG.vertexFormatCache.get()) {
            VertexFormatCacheAPI.addCacheableFormats(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
            debug("Cacheable vertex formats added.");
        }
    }

    public static void debug(String msg) {
        if (ToadLibClient.printDebug()) {
            LOGGER.debug(msg);
        }
    }
}

