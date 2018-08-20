package com.peridotapps.nitro.random;

import android.support.annotation.Nullable;

import java.util.Random;

public final class RandomFloat extends RandomNumber<Float> {

    public RandomFloat() {
        super(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public RandomFloat(float maxValue) {
        super(Float.MIN_VALUE, maxValue);
    }

    public RandomFloat(float minValue, float maxValue) {
        super(minValue, maxValue);
    }

    @Override
    @Nullable
    protected Float onGenerate(Random r) {
        float randomVal = r.nextFloat();

        return (randomVal >= getMinValue() && randomVal <= getMaxValue()) ? randomVal : generate();
    }
}
