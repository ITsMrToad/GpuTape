package com.mr_toad.gpu_booster.client.config;

import com.mojang.serialization.Codec;
import com.mr_toad.gpu_booster.client.GPUBooster;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum DSAMode implements StringIdentifiable {

    ALL("all"),
    VBO("vbo"),
    FBO("fbo"),
    OFF("disabled");

    public static final Codec<DSAMode> CODEC = StringIdentifiable.createCodec(DSAMode::values);

    private final String name;
    private final Text text;

    DSAMode(String name) {
        this.name = name;
        this.text = Text.translatable("gb.config.dsa." + name);
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Text symbol() {
        return this.text;
    }

}
