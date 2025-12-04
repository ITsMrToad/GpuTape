package com.mr_toad.gpu_booster.client.resource;

import com.mr_toad.gpu_booster.client.GPUBooster;
import com.mr_toad.gpu_booster.client.rendering.gl.VertexBufferCache;
import com.mr_toad.lib.core.ToadLibClient;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

public class UnregisteredIdsResourceSupplier extends SinglePreparationResourceReloader<Void> implements IdentifiableResourceReloadListener {

    private static final Identifier RESOURCE_ID = Identifier.of(GPUBooster.MODID, "unregistered_ids");

    @Override
    public Identifier getFabricId() {
        return RESOURCE_ID;
    }

    @Override
    protected Void prepare(ResourceManager manager, Profiler profiler) {
        GPUBooster.debug("Clear unregistered VBO/EBO IDs.");
        return null;
    }

    @Override
    protected void apply(Void prepared, ResourceManager manager, Profiler profiler) {
        VertexBufferCache.deleteUnregistered();
    }
}
