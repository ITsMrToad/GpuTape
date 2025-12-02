package com.mr_toad.gpu_booster.client.rendering.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.GaussianGenerator;
import net.minecraft.util.math.random.Random;

public class TableGaussianGenerator extends GaussianGenerator {

    private static final int TABLE_SIZE = 256;
    private static final float R = 3.442619855899F;
    private static final float INV_R = 0.29047645161474317F;

    private static final float[] X = new float[TABLE_SIZE + 1];
    private static final float[] Y = new float[TABLE_SIZE + 1];

    static {
        float f = GBFMath.fastExp(-0.5F * R * R);
        X[0] = R / f;
        X[1] = R;
        Y[0] = 0.0F;
        Y[1] = f;

        for (int i = 2; i <= TABLE_SIZE; i++) {
            X[i] = MathHelper.sqrt(-2.0F * GBFMath.fastLog(GBFMath.fastExp(-0.5F * X[i - 1] * X[i - 1]) + Y[i - 1] / X[i - 1]));
            Y[i] = GBFMath.fastExp(-0.5F * X[i] * X[i]);
        }
    }

    public TableGaussianGenerator(Random rand) {
        super(rand);
    }

    @Override
    public void reset() {}

    @Override
    public double next() {
        while (true) {
            int i = this.baseRandom.nextInt(TABLE_SIZE);
            long j = this.baseRandom.nextInt() & 0xFFFFFFFFL;

            double xVal = j * (X[i] / 4294967296.0);

            if (Math.abs(xVal) < X[i + 1]) {
                return xVal;
            }

            if (i == 0) {
                double xx, yy;
                do {
                    xx = -GBFMath.fastLog((float) this.baseRandom.nextDouble()) * INV_R;
                    yy = -GBFMath.fastLog((float) this.baseRandom.nextDouble());
                } while (yy + yy < xx * xx);
                return (this.baseRandom.nextBoolean() ? R + xx : -R - xx);
            }

            if (Y[i + 1] + (Y[i] - Y[i + 1]) * this.baseRandom.nextDouble() < GBFMath.fastExp((float) (-0.5 * xVal * xVal))) {
                return this.baseRandom.nextBoolean() ? xVal : -xVal;
            }
        }
    }
}
