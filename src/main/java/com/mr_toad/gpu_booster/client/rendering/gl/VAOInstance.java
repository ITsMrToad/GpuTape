package com.mr_toad.gpu_booster.client.rendering.gl;

import com.google.common.annotations.Beta;
import com.mojang.blaze3d.platform.GlConst;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

@Beta
public class VAOInstance {

    private final VertexFormat format;
    private final int vao;

    public VAOInstance(VertexFormat format) {
        this.format = format;
        this.vao = GBGL.createVAO();
    }

    public void setupFormat() {
        for (int j = 0; j < this.format.getElements().size(); j++) {
            VertexFormatElement element = this.format.getElements().get(j);
            int count = element.count();
            int type = element.type().getGlType();
            int offset = this.format.getOffset(element);
            boolean normalized = (element.usage() == VertexFormatElement.Usage.NORMAL || element.usage() == VertexFormatElement.Usage.COLOR);
            GBGL.enableVAOAttrib(this.vao, j);
            if (element.usage() == VertexFormatElement.Usage.UV && type != GlConst.GL_FLOAT) {
                GBGL.vaoFormat(this.vao, j, count, type, offset);
            } else {
                GBGL.vaoFormat(this.vao, j, count, type, normalized, offset);
            }
            GBGL.bindVAOAttrib(this.vao, j, 0);
        }
    }

    public VertexFormat getFormat() {
        return this.format;
    }

    public int getID() {
        return this.vao;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof VAOInstance instance)) {
            return false;
        } else {
            return this.getFormat().equals(instance.getFormat());
        }
    }

    @Override
    public int hashCode() {
        return this.format.hashCode();
    }
}
