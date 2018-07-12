package com.peridotapps.nitro.random;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.peridotapps.nitro.concurrent.RunnableTask;

import java.util.Random;

public abstract class Randomizer<T> {

    protected abstract T onGenerate(Random r);

    public final void generate(@NonNull RandomCallback<T> callback) {
        generateAsync(callback).execute();
    }

    @Nullable
    public final T generate() {
        try {
            return onGenerate(RandomUtil.getRandom());
        } catch (Exception e) {
            return null;
        }
    }

    private RunnableTask generateAsync(@NonNull final RandomCallback<T> callback) {
        return new RunnableTask() {

            private T value;

            @Override
            protected void onRun() throws Exception {
                value = onGenerate(RandomUtil.getRandom());
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                callback.onGenerated(value);
            }

            @Override
            public void onFailed(Throwable t) {
                super.onFailed(t);
                callback.onGenerated(null);
            }
        };
    }

    public interface RandomCallback<T> {
        void onGenerated(T characters);
    }
}