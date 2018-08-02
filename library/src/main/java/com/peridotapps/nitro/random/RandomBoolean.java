package com.peridotapps.nitro.random;

import android.support.annotation.Nullable;

import java.util.Random;

public final class RandomBoolean extends Randomizer<Boolean> {

  public RandomBoolean () {

  }

  @Override
  @Nullable
  protected Boolean onGenerate (Random r) {
    return r.nextBoolean();
  }
}
