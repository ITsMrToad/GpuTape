package com.mr_toad.gpu_booster.client.rendering.math;

import com.mr_toad.lib.mtjava.math.MtMath;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class GBFMath {

    public static final float SINGULAR = 1E-9F;
    public static final float NORMALIZE_EPSILON = 1E-12F;

    private static final int MAGIC = 0x5F3759DF;
    private static final int EXP_LUT_SIZE = 2048;
    private static final float EXP_LUT_SCALE = 256.0F;
    private static final float[] EXP_LUT = Util.make(new float[EXP_LUT_SIZE + 1], arr -> {
        for (int i = 0; i <= EXP_LUT_SIZE; i++) {
            arr[i] = (float) Math.exp(i / EXP_LUT_SCALE - 8.0F);
        }
    });

    public static float invLen(float x, float y, float z) {
        return 1.0F / MtMath.length(x, y, z);
    }

    public static float invsqrt(float x) {
        if (Float.isNaN(x)) {
            return Float.NaN;
        } else if (x == 0.0F) {
            return Float.POSITIVE_INFINITY;
        } else if (x < 0.0F) {
            return Float.NaN;
        } else if (Float.isInfinite(x)) {
            return 0.0F;
        } else {
            float y = Float.intBitsToFloat(MAGIC - (Float.floatToRawIntBits(x) >> 1));
            y *= 1.5F - 0.5F * x * y * y;
            y *= 1.5F - 0.5F * x * y * y;
            return y;
        }
    }

    public static float cosFromSin(float sinHalf, float halfAngle) {
        if (Float.isNaN(sinHalf) || Float.isNaN(halfAngle)) {
            return Float.NaN;
        }

        float s = sinHalf;
        if (s > 1.0F) {
            s = 1.0F;
        } else if (s < -1.0F) {
            s = -1.0f;
        }
        float t = 1.0F - s * s;
        if (t <= 0.0F) {
            return Math.copySign(0.0F, halfAngle);
        }

        float cosAbs = t * invsqrt(t);
        return Math.copySign(cosAbs, MathHelper.cos(halfAngle));
    }

    public static float fastLog(float x) {
        return (Float.floatToRawIntBits(x) - 1064866805) * 0.00009015944F;
    }

    public static float fastExp(float x) {
        if (x <= -8F) {
            return 0F;
        } else if (x >= 8F) {
            return (float) Math.exp(8F);
        } else {
            float fx = (x + 8F) * EXP_LUT_SCALE;
            int ix = (int) fx;
            float t = fx - ix;
            return EXP_LUT[ix] + t * (EXP_LUT[ix + 1] - EXP_LUT[ix]);
        }
    }
}
