package com.mr_toad.gpu_tape.client;

import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GpuTape implements ClientModInitializer {

	public static final ConcurrentLinkedQueue<FramebufferFixer> FIXERS = Queues.newConcurrentLinkedQueue();

	@Override
	public void onInitialize() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			try {
				boolean done = false;
				int counter = 0;
				while(!FIXERS.isEmpty() && counter++ < 20) {
					FramebufferFixer fixer = FIXERS.poll();
					if (fixer != null) {
						if (!done) {
							fixer.destroy();
							done = true;
						}
						fixer.release();
					}
				}
			} catch (Exception e) {
				throw new CleanException("Failed to proccess cleaning framebuffer!", e);
			}
		});
	}

        public static boolean isVulkan() {
		return FabricLoader.getInstance().isModLoaded("vulkanmod");
	}
}
