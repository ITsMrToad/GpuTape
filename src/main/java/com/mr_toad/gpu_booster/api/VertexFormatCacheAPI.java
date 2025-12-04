package com.mr_toad.gpu_booster.api;

import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.rendering.gl.VertexBufferCache;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.render.VertexFormat;

import java.util.Collections;

public class VertexFormatCacheAPI {

    private static final ObjectList<VertexFormat> CACHEABLE = new ObjectArrayList<>();

    public static void addCacheableFormat(VertexFormat format) {
        CACHEABLE.add(format);
    }

    public static void addCacheableFormats(VertexFormat... formats) {
        Collections.addAll(CACHEABLE, formats);
    }

    public static boolean canBeCached(VertexFormat format) {
        return GPUBooster.CONFIG.vertexFormatCache.get() && CACHEABLE.contains(format);
    }

    public static void deleteUnregistered() {
        VertexBufferCache.deleteUnregistered();
    }
}
