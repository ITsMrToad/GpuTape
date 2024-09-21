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

	public static final Logger LOGGER = LoggerFactory.getLogger("VideoTape");
	public static final String MODID = "video_tape_resurrected";

	public static final ConcurrentLinkedQueue<Framebuffer> FRAMEBUFFERS = Queues.newConcurrentLinkedQueue();

	@Override
	public void onInitialize() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			boolean done = false;
			int counter = 0;

			while(!FRAMEBUFFERS.isEmpty() && counter++ < 20) {
				if (!done) {
					GlStateManager._bindTexture(0);
					GlStateManager._glBindFramebuffer(36160, 0);
					done = true;
				}

				Framebuffer ids = FRAMEBUFFERS.poll();
				if (ids != null) {
					if (ids.getColorAttachment() > -1) {
						TextureUtil.releaseTextureId(ids.getColorAttachment());
					}

					if (ids.getDepthAttachment() > -1) {
						TextureUtil.releaseTextureId(ids.getDepthAttachment());
					}

					if (ids.fbo > -1) {
						GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
						GlStateManager._glDeleteFramebuffers(ids.fbo);
					}
				}
			}
		});
	}

}
