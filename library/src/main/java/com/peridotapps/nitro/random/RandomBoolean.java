package com.peridotapps.nitro.random;

import android.support.annotation.NonNull;

import java.util.Random;

public final class RandomBoolean extends Randomizer<Boolean> {

    public RandomBoolean() {

    }

    @Override
    @NonNull
    protected Boolean onGenerate(Random r) {
        return r.nextBoolean();
    }
}
