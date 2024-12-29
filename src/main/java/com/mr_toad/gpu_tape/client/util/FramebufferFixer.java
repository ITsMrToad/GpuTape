package com.mr_toad.gpu_tape.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface FramebufferFixer {
    void destroy();

    void release();
}
