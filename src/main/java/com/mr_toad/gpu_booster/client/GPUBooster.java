package com.mr_toad.gpu_booster.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mr_toad.gpu_booster.client.config.DSAMode;
import com.mr_toad.gpu_booster.client.config.GBConfig;
import com.mr_toad.gpu_booster.client.rendering.gl.GBGL;
import com.mr_toad.gpu_booster.client.rendering.gl.VertexFormatCache;
import com.mr_toad.lib.api.config.ToadConfigs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GPUBooster implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("GPUBooster");
    public static final String MODID = "gpu_booster";
    public static final Identifier RENDER_CYCLE = Identifier.of(MODID, "render_cycle");
    public static final GBConfig CONFIG = new GBConfig();

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
        });
        ToadConfigs.create(MODID, CONFIG);
        System.setProperty("joml.fastmath", CONFIG.fastMath.get().toString());
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return RENDER_CYCLE;
            }

            @Override
            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
                VertexFormatCache.deleteUnregistered();
                GPUBooster.LOGGER.debug("Clear unregistered VBO/EBO IDs.");
                return null;
            }
        });
    }
}
