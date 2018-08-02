package com.peridotapps.nitro.random;

import android.support.annotation.NonNull;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

class RandomUtil {
  private RandomUtil () {
  }

  private final static AtomicReference<Random> randomAtomicReference = new AtomicReference<>(initRandom(System.currentTimeMillis()));
  
  @NonNull
  private static Random initRandom (long seed) {
    return new Random(seed);
  }

  private static void resetRandom () {
    synchronized (randomAtomicReference) {
      Random random = randomAtomicReference.get();
      long seed = random.nextLong();
      randomAtomicReference.set(initRandom(seed));
    }
  }
  
  @NonNull
  static Random getRandom () {
    resetRandom();
    
    Random random;
    synchronized (randomAtomicReference) {
      random = randomAtomicReference.get();
    }
    
    return random;
  }
}