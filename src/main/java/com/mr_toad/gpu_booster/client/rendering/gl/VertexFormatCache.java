package com.mr_toad.gpu_booster.client.rendering.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mr_toad.gpu_booster.api.GBGL;
import com.mr_toad.gpu_booster.api.IntArrayDeque;
import com.mr_toad.gpu_booster.api.VertexFormatCacheAPI;
import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.lib.api.client.utils.graphics.gl.GLU;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.VertexFormat;

public class VertexFormatCache {

    private static final Object2ObjectMap<VertexFormat, VAOInstance> VAOS = new Object2ObjectOpenHashMap<>();

    private static final IntArrayDeque VBO_POOL = new IntArrayDeque(GPUBooster.CONFIG.renderCyclePoolSize.get());
    private static final IntArrayDeque EBO_POOL = new IntArrayDeque(GPUBooster.CONFIG.renderCyclePoolSize.get());

    private static final IntList UNREGISTERED_IDS = new IntArrayList();

    public static void preGen() {
        for (int i = 0; i < GPUBooster.CONFIG.renderCyclePoolSize.get(); i++) {
            VBO_POOL.addFirst(GBGL.createVBO());
            EBO_POOL.addFirst(GBGL.createVBO());
        }
    }

    public static int getVBO() {
        RenderSystem.assertOnRenderThread();
        return VBO_POOL.pool().orElseGet(() -> {
            GPUBooster.debug("VBO pool is empty!");
            int i = GBGL.createVBO();
            UNREGISTERED_IDS.add(i);
            return i;
        });
    }

    public static int getEBO() {
        RenderSystem.assertOnRenderThread();
        return EBO_POOL.pool().orElseGet(() -> {
            GPUBooster.debug("EBO pool is empty!");
            int i = GBGL.createVBO();
            UNREGISTERED_IDS.add(i);
            return i;
        });
    }

    public static int getVAO(VertexFormat format) {
        RenderSystem.assertOnRenderThread();
        if (VertexFormatCacheAPI.canBeCached(format)) {
            VAOInstance instance = VAOS.get(format);
            if (instance != null) {
                return instance.getID();
            } else {
                VAOInstance id = VAOInstance.makeVAO(format);
                VAOS.put(format, id);
                return id.getID();
            }
        } else {
            return VAOInstance.makeVAO(format).getID();
        }
    }

    public static void clean(int vao, int vbo, int ebo) {
        GBGL.addEBO2VAO(vao, 0);
        GBGL.addVAO2VBO(vao, 0, 0, 0L, 0);
        VBO_POOL.addLast(vbo);
        EBO_POOL.addLast(ebo);
    }

    public static void deleteUnregistered() {
        if (!UNREGISTERED_IDS.isEmpty()) {
            UNREGISTERED_IDS.forEach(GlStateManager::_glDeleteBuffers);
        }
    }
}

