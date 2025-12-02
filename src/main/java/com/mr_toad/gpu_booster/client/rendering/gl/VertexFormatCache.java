package com.mr_toad.gpu_booster.client.rendering.gl;

import com.google.common.annotations.Beta;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.rendering.util.IntArrayDeque;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.VertexFormat;
import org.lwjgl.opengl.GL15;

@Beta
public class VertexFormatCache {

    @Deprecated private static final Object2ObjectMap<VertexFormat, VAOInstance> VAOS = new Object2ObjectOpenHashMap<>();

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
            GPUBooster.LOGGER.warn("VBO pool is empty!");
            int i = GBGL.createVBO();
            UNREGISTERED_IDS.add(i);
            return i;
        });
    }

    public static int getEBO() {
        RenderSystem.assertOnRenderThread();
        return EBO_POOL.pool().orElseGet(() -> {
            GPUBooster.LOGGER.warn("EBO pool is empty!");
            int i = GBGL.createVBO();
            UNREGISTERED_IDS.add(i);
            return i;
        });
    }

    public static int getVAO(VertexFormat format) {
        RenderSystem.assertOnRenderThread();
        VAOInstance instance = new VAOInstance(format);
        instance.setupFormat();
        return instance.getID();
    }

    public static void clean(int vao, int vbo, int ebo, VertexBuffer.Usage usage) {
        GBGL.addEBO2VAO(vao, 0);
        GBGL.addVAO2VBO(vao, 0, 0, 0L, 0);
        GlStateManager._glDeleteVertexArrays(vao);

       // VAOInstance instance = VAOS.get(format);
   //     if (instance != null) {
        //    instance.clearVBO();
         //   GBGL.addEBO2VAO(instance.getID(), 0);
      //  }
        int i = usage == VertexBuffer.Usage.DYNAMIC ? GL15.GL_DYNAMIC_DRAW : GL15.GL_STATIC_DRAW;
        GBGL.namedBufferData(vbo, null, i);
        GBGL.namedBufferData(ebo, null, i);

        VBO_POOL.addLast(vbo);
        EBO_POOL.addLast(ebo);
    }

    public static void deleteUnregistered() {
        if (!UNREGISTERED_IDS.isEmpty()) {
            UNREGISTERED_IDS.forEach(GlStateManager::_glDeleteBuffers);
        }
    }
}
