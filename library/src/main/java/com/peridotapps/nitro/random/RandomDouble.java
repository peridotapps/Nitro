package com.peridotapps.nitro.random;

import android.support.annotation.Nullable;

import java.util.Random;

public final class RandomDouble extends RandomNumber<Double> {
  
  public RandomDouble () {
    super(Double.MIN_VALUE, Double.MAX_VALUE);
  }
  
  public RandomDouble (double maxValue) {
    super(Double.MIN_VALUE, maxValue);
  }
  
  public RandomDouble (double minValue, double maxValue) {
    super(minValue, maxValue);
  }
  
  @Override
  @Nullable
  protected Double onGenerate (Random r) {
    Double randomVal = r.nextDouble();
    
    return (randomVal >= getMinValue() && randomVal <= getMaxValue()) ? randomVal : generate();
  }
}
