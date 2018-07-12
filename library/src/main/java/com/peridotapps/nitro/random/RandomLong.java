package com.peridotapps.nitro.random;

import android.support.annotation.Nullable;

import java.util.Random;

public final class RandomLong extends RandomNumber<Long> {

    public RandomLong() {
        super(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public RandomLong(long maxValue) {
        super(Long.MIN_VALUE, maxValue);
    }

    public RandomLong(long minValue, long maxValue) {
        super(minValue, maxValue);
    }

    @Override
    @Nullable
    protected Long onGenerate(Random random) {
        Long randomValue = random.nextLong();

        return (randomValue <= getMaxValue() && randomValue >= getMinValue())
                ? randomValue
                : generate();
    }
}
