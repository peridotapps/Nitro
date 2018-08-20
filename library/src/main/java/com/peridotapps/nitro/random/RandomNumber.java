package com.peridotapps.nitro.random;

import android.support.annotation.NonNull;

public abstract class RandomNumber<T extends Number> extends Randomizer<T> {

    private T minValue;
    private T maxValue;

    public RandomNumber(@NonNull T minValue, @NonNull T maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @NonNull
    public T getMinValue() {
        return minValue;
    }

    @NonNull
    public T getMaxValue() {
        return maxValue;
    }

}
