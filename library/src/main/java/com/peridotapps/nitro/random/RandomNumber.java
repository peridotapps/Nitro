package com.peridotapps.nitro.random;

public abstract class RandomNumber<T extends Number> extends Randomizer<T> {

    private T minValue;
    private T maxValue;

    public RandomNumber(T minValue, T maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }

}
