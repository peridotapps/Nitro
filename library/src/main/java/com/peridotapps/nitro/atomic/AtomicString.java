package com.peridotapps.nitro.atomic;

import java.util.concurrent.atomic.AtomicReference;

public final class AtomicString extends AtomicReference<String> {

    public AtomicString() {
        super();
    }

    public AtomicString(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return get();
    }
}
