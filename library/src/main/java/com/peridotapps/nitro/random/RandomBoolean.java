package com.peridotapps.nitro.random;

import java.util.Random;

public final class RandomBoolean extends Randomizer<Boolean> {

  public RandomBoolean () {

  }

  @Override
  protected Boolean onGenerate (Random r) {
    return r.nextBoolean();
  }
}
