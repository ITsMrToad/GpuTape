package com.mr_toad.gpu_booster.client.rendering.math;

import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public class GBFMatrix3f extends Matrix3f {

    public GBFMatrix3f() {
        super();
    }

    public GBFMatrix3f(Matrix3f src) {
        super(src);
    }

    @Override
    public GBFMatrix3f transpose() {
        float t;
        t = this.m01();
        this.m01(this.m10());
        this.m10(t);
        t = this.m02();
        this.m02(this.m20());
        this.m20(t);
        t = this.m12();
        this.m12(this.m21());
        this.m21(t);
        return this;
    }

    @Override
    public GBFMatrix3f mul(Matrix3fc right) {
        float a00 = this.m00(), a01 = this.m01(), a02 = this.m02();
        float a10 = this.m10(), a11 = this.m11(), a12 = this.m12();
        float a20 = this.m20(), a21 = this.m21(), a22 = this.m22();

        float b00 = right.m00(), b01 = right.m01(), b02 = right.m02();
        float b10 = right.m10(), b11 = right.m11(), b12 = right.m12();
        float b20 = right.m20(), b21 = right.m21(), b22 = right.m22();

        this.m00(a00 * b00 + a01 * b10 + a02 * b20);
        this.m01(a00 * b01 + a01 * b11 + a02 * b21);
        this.m02(a00 * b02 + a01 * b12 + a02 * b22);

        this.m10(a10 * b00 + a11 * b10 + a12 * b20);
        this.m11(a10 * b01 + a11 * b11 + a12 * b21);
        this.m12(a10 * b02 + a11 * b12 + a12 * b22);

        this.m20(a20 * b00 + a21 * b10 + a22 * b20);
        this.m21(a20 * b01 + a21 * b11 + a22 * b21);
        this.m22(a20 * b02 + a21 * b12 + a22 * b22);

        return this;
    }

    @Override
    public GBFMatrix3f invert() {
        float a00 = this.m00(), a01 = this.m01(), a02 = this.m02();
        float a10 = this.m10(), a11 = this.m11(), a12 = this.m12();
        float a20 = this.m20(), a21 = this.m21(), a22 = this.m22();

        float co00 = a11 * a22 - a12 * a21;
        float co10 = -(a10 * a22 - a12 * a20);
        float co20 = a10 * a21 - a11 * a20;

        float det = a00 * co00 + a01 * co10 + a02 * co20;
        if (Math.abs(det) <= GBFMath.SINGULAR) {
            super.invert();
            return this;
        }
        float invDet = 1.0f / det;

        float i00 = co00 * invDet;
        float i01 = (-(a01 * a22 - a02 * a21)) * invDet;
        float i02 = ((a01 * a12 - a02 * a11)) * invDet;

        float i10 = co10 * invDet;
        float i11 = (a00 * a22 - a02 * a20) * invDet;
        float i12 = (-(a00 * a12 - a02 * a10)) * invDet;

        float i20 = co20 * invDet;
        float i21 = (-(a00 * a21 - a01 * a20)) * invDet;
        float i22 = (a00 * a11 - a01 * a10) * invDet;

        this.m00(i00);
        this.m01(i01);
        this.m02(i02);
        this.m10(i10);
        this.m11(i11);
        this.m12(i12);
        this.m20(i20);
        this.m21(i21);
        this.m22(i22);

        return this;
    }
}
