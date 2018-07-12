package com.peridotapps.nitro.random;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

class RandomUtil {
    private RandomUtil() {
    }

    private final static AtomicReference<Random> randomAtomicReference =
            new AtomicReference<>(initRandom(System.currentTimeMillis()));

    private static Random initRandom(long seed) {
        return new Random(seed);
    }

    private static void resetRandom() {
        Random random = getRandom();
        long seed = random.nextLong();
        randomAtomicReference.set(initRandom(seed));
    }

    static Random getRandom() {
        Random random;
        synchronized (randomAtomicReference) {
            random = randomAtomicReference.get();
        }
        resetRandom();
        return random;
    }
}