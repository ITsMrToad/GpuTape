package com.mr_toad.gpu_booster.client.rendering.math;

import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class GBFMatrix4f extends Matrix4f {

    public GBFMatrix4f() {
        super();
    }

    public GBFMatrix4f(Matrix4f src) {
        super(src);
    }

    @Override
    public GBFMatrix4f rotateX(float ang) {
        float c = MathHelper.cos(ang);
        float s = MathHelper.sin(ang);
        this.applyRotation(1.0F, 0.0F, 0.0F, 0.0F, c, -s, 0.0F, s,  c);
        return this;
    }

    @Override
    public GBFMatrix4f rotateY(float ang) {
        float c = MathHelper.cos(ang);
        float s = MathHelper.sin(ang);
        this.applyRotation(c, 0.0F, s, 0.0F, 1.0F, 0.0F, -s, 0.0F, c);
        return this;
    }

    @Override
    public GBFMatrix4f rotateZ(float ang) {
        float c = MathHelper.cos(ang);
        float s = MathHelper.sin(ang);
        this.applyRotation(c, -s, 0f, s,  c, 0f, 0f, 0f, 1f);
        return this;
    }

    @Override
    public GBFMatrix4f rotate(float ang, float x, float y, float z) {
        float invLen = GBFMath.invLen(x, y, z);
        x *= invLen; y *= invLen; z *= invLen;

        float c = MathHelper.cos(ang);
        float s = MathHelper.sin(ang);
        float t = 1.0f - c;

        float r00 = c + x * x * t;
        float r01 = x * y * t - z * s;
        float r02 = x * z * t + y * s;
        float r10 = y * x * t + z * s;
        float r11 = c + y * y * t;
        float r12 = y * z * t - x * s;
        float r20 = z * x * t - y * s;
        float r21 = z * y * t + x * s;
        float r22 = c + z * z * t;

        this.applyRotation(r00, r01, r02, r10, r11, r12, r20, r21, r22);

        return this;
    }

    @Override
    public GBFMatrix4f rotateXYZ(float angleX, float angleY, float angleZ) {
        float cx = MathHelper.cos(angleX), sx = MathHelper.sin(angleX);
        float cy = MathHelper.cos(angleY), sy = MathHelper.sin(angleY);
        float cz = MathHelper.cos(angleZ), sz = MathHelper.sin(angleZ);

        float r00 = cy * cz;
        float r01 = -cy * sz;

        float r10 = sx * sy * cz + cx * sz;
        float r11 = -sx * sy * sz + cx * cz;
        float r12 = -sx * cy;

        float r20 = -cx * sy * cz + sx * sz;
        float r21 = cx * sy * sz + sx * cz;
        float r22 = cx * cy;

        this.applyRotation(r00, r01, sy, r10, r11, r12, r20, r21, r22);
        return this;
    }

    @Override
    public GBFMatrix4f mul(Matrix4fc right) {
        float a00 = this.m00(), a01 = this.m01(), a02 = this.m02(), a03 = this.m03();
        float a10 = this.m10(), a11 = this.m11(), a12 = this.m12(), a13 = this.m13();
        float a20 = this.m20(), a21 = this.m21(), a22 = this.m22(), a23 = this.m23();
        float a30 = this.m30(), a31 = this.m31(), a32 = this.m32(), a33 = this.m33();

        float b00 = right.m00(), b01 = right.m01(), b02 = right.m02(), b03 = right.m03();
        float b10 = right.m10(), b11 = right.m11(), b12 = right.m12(), b13 = right.m13();
        float b20 = right.m20(), b21 = right.m21(), b22 = right.m22(), b23 = right.m23();
        float b30 = right.m30(), b31 = right.m31(), b32 = right.m32(), b33 = right.m33();

        this.m00(a00 * b00 + a01 * b10 + a02 * b20 + a03 * b30);
        this.m01(a00 * b01 + a01 * b11 + a02 * b21 + a03 * b31);
        this.m02(a00 * b02 + a01 * b12 + a02 * b22 + a03 * b32);
        this.m03(a00 * b03 + a01 * b13 + a02 * b23 + a03 * b33);

        this.m10(a10 * b00 + a11 * b10 + a12 * b20 + a13 * b30);
        this.m11(a10 * b01 + a11 * b11 + a12 * b21 + a13 * b31);
        this.m12(a10 * b02 + a11 * b12 + a12 * b22 + a13 * b32);
        this.m13(a10 * b03 + a11 * b13 + a12 * b23 + a13 * b33);

        this.m20(a20 * b00 + a21 * b10 + a22 * b20 + a23 * b30);
        this.m21(a20 * b01 + a21 * b11 + a22 * b21 + a23 * b31);
        this.m22(a20 * b02 + a21 * b12 + a22 * b22 + a23 * b32);
        this.m23(a20 * b03 + a21 * b13 + a22 * b23 + a23 * b33);

        this.m30(a30 * b00 + a31 * b10 + a32 * b20 + a33 * b30);
        this.m31(a30 * b01 + a31 * b11 + a32 * b21 + a33 * b31);
        this.m32(a30 * b02 + a31 * b12 + a32 * b22 + a33 * b32);
        this.m33(a30 * b03 + a31 * b13 + a32 * b23 + a33 * b33);

        return this;
    }

    @Override
    public GBFMatrix4f transpose() {
        float t;
        t = this.m01();
        this.m01(this.m10());
        this.m10(t);
        t = this.m02();
        this.m02(this.m20());
        this.m20(t);
        t = this.m03();
        this.m03(this.m30());
        this.m30(t);
        t = this.m12();
        this.m12(this.m21());
        this.m21(t);
        t = this.m13();
        this.m13(this.m31());
        this.m31(t);
        t = this.m23();
        this.m23(this.m32());
        this.m32(t);
        return this;
    }

    @Override
    public GBFMatrix4f invert() {
        float a00 = this.m00(), a01 = this.m12(), a02 = this.m02();
        float a10 = this.m10(), a11 = this.m11(), a12 = this.m12();
        float a20 = this.m20(), a21 = this.m21(), a22 = this.m22();
        float tx = this.m03(), ty = this.m13(), tz = this.m23();

        float co00 = a11 * a22 - a12 * a21;
        float co01 = a02 * a21 - a01 * a22;
        float co02 = a01 * a12 - a02 * a11;

        float co10 = a12 * a20 - a10 * a22;
        float co11 = a00 * a22 - a02 * a20;
        float co12 = a02 * a10 - a00 * a12;

        float co20 = a10 * a21 - a11 * a20;
        float co21 = a01 * a20 - a00 * a21;
        float co22 = a00 * a11 - a01 * a10;

        float det = a00 * co00 + a01 * co10 + a02 * co20;
        if (Math.abs(det) <= GBFMath.SINGULAR) {
            super.invert();
            return this;
        }
        float invDet = 1.0f / det;

        float i00 = co00 * invDet;
        float i01 = co01 * invDet;
        float i02 = co02 * invDet;
        float i10 = co10 * invDet;
        float i11 = co11 * invDet;
        float i12 = co12 * invDet;
        float i20 = co20 * invDet;
        float i21 = co21 * invDet;
        float i22 = co22 * invDet;

        float n03 = -(i00 * tx + i01 * ty + i02 * tz);
        float n13 = -(i10 * tx + i11 * ty + i12 * tz);
        float n23 = -(i20 * tx + i21 * ty + i22 * tz);

        this.m00(i00);
        this.m01(i01);
        this.m02(i02);
        this.m03(n03);
        this.m10(i10);
        this.m11(i11);
        this.m12(i12);
        this.m13(n13);
        this.m10(i20);
        this.m11(i21);
        this.m12(i22);
        this.m13(n23);

        this.affineIdentity();
        return this;
    }

    private void applyRotation(float r00, float r01, float r02, float r10, float r11, float r12, float r20, float r21, float r22) {
        float a00 = this.m00(), a01 = this.m01(), a02 = this.m02();
        float a10 = this.m10(), a11 = this.m11(), a12 = this.m12();
        float a20 = this.m20(), a21 = this.m21(), a22 = this.m22();
        float a03 = this.m03(), a13 = this.m13(), a23 = this.m23(); 

        this.m00(a00 * r00 + a01 * r10 + a02 * r20);
        this.m01(a00 * r01 + a01 * r11 + a02 * r21);
        this.m02(a00 * r02 + a01 * r12 + a02 * r22);
        this.m03(a03);

        this.m10(a10 * r00 + a11 * r10 + a12 * r20);
        this.m11(a10 * r01 + a11 * r11 + a12 * r21);
        this.m12(a10 * r02 + a11 * r12 + a12 * r22);
        this.m13(a13);

        this.m20(a20 * r00 + a21 * r10 + a22 * r20);
        this.m21(a20 * r01 + a21 * r11 + a22 * r21);
        this.m22(a20 * r02 + a21 * r12 + a22 * r22);
        this.m23(a23);
    }
    
    private void affineIdentity() {
        this.m30(0.0F);
        this.m31(0.0F);
        this.m32(0.0F);
        this.m33(1.0F);
    }
}
