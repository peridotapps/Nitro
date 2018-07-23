package com.peridotapps.nitro.random;

import android.support.annotation.Nullable;
import java.util.Random;

public final class RandomInteger extends RandomNumber<Integer> {

  public RandomInteger () {
    super(Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  public RandomInteger (int maxValue) {
    super(Integer.MIN_VALUE, maxValue);
  }

  public RandomInteger (int minValue, int maxValue) {
    super(minValue, maxValue);
  }

  @Override
  @Nullable
  protected Integer onGenerate (Random random) {
    int randomValue = (getMaxValue() != Integer.MAX_VALUE) ? random.nextInt(getMaxValue()) : random.nextInt();

    return (randomValue >= getMinValue()) ? randomValue : generate();
  }
}
