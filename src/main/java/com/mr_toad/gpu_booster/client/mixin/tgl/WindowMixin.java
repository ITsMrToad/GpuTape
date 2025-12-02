package com.mr_toad.gpu_booster.client.mixin.tgl;

import com.mr_toad.gpu_booster.client.GPUBooster;
import net.minecraft.client.util.Window;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//Update the OpenGL Context(ThreatenGL mod)
//https://github.com/Richy-Z/ThreatenGL/blob/main/common/src/main/java/lol/richy/threatengl/mixin/ThreatenGLMixin.java
@Mixin(value = Window.class, priority = 1001)
public abstract class WindowMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwWindowHint(II)V", remap = false))
    private void overrideGLCTX(int hint, int value) {
        boolean isMacOS = Util.getOperatingSystem() == Util.OperatingSystem.OSX;
        if (hint == GLFW.GLFW_CONTEXT_VERSION_MAJOR) {
            value = 4;
        } else if (hint == GLFW.GLFW_CONTEXT_VERSION_MINOR) {
            if (isMacOS) {
                GPUBooster.LOGGER.info("The maximum supported version of OpenGL on MacOS is 4.1.");
            }
            value = isMacOS ? 1 : 6;
        }
        GLFW.glfwWindowHint(hint, value);
    }
}
