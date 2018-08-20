package com.peridotapps.nitro.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.peridotapps.nitro.NitroApplication;
import com.peridotapps.nitro.atomic.AtomicString;
import com.peridotapps.nitro.logging.Logger;

import java.io.InvalidClassException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PreferenceLibrary {

    @NonNull
    private static final String DEFAULT_LIBRARY_NAME = "application.default.preferences";

    @NonNull
    private final AtomicInteger accessMode = new AtomicInteger(Context.MODE_PRIVATE);

    @NonNull
    private final AtomicString preferenceLibraryIdentifier = new AtomicString(DEFAULT_LIBRARY_NAME);

    @NonNull
    private final AtomicReference<SharedPreferences> sharedPreferences = new AtomicReference<>();

    @NonNull
    private final AtomicReference<Map<String, Object>> preferenceCache = new AtomicReference<>(new TreeMap<>());

    private PreferenceLibrary() {
    }

    private void setPreferenceCache(Map<String, ?> all) {
        synchronized (preferenceCache) {
            this.preferenceCache.get()
                    .putAll(all);
        }
    }

    @NonNull
    private Map<String, Object> getPreferenceCache() {
        Map<String, Object> prefCache;
        synchronized (preferenceCache) {
            prefCache = preferenceCache.get();
        }
        return prefCache;
    }

    private void setAccessMode(int accessMode) {
        synchronized (this.accessMode) {
            this.accessMode.set(accessMode);
        }
    }

    private int getAccessMode() {
        synchronized (this.accessMode) {
            return this.accessMode.get();
        }
    }

    private void setLibraryIdentifier(String libraryIdentifier) {
        synchronized (preferenceLibraryIdentifier) {
            this.preferenceLibraryIdentifier.set(libraryIdentifier);
        }
    }

    @NonNull
    private String getLibraryIdentifier() {
        synchronized (preferenceLibraryIdentifier) {
            return preferenceLibraryIdentifier.get();
        }
    }

    @NonNull
    private SharedPreferences getSharedPreferences() {
        synchronized (sharedPreferences) {
            if (sharedPreferences.get() == null) {
                this.sharedPreferences.set(NitroApplication.getSharedInstance()
                        .getSharedPreferences(getLibraryIdentifier(), getAccessMode()));
            }
        }

        return this.sharedPreferences.get();
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private <T> T readPreference(@NonNull String identifier, @NonNull T defaultValue) {

        SharedPreferences preferences = getSharedPreferences();
        Map<String, Object> preferencesCache = getPreferenceCache();

        Object returnValue = preferencesCache.containsKey(identifier) ? preferencesCache.get(identifier) : (preferences.getAll()
                .containsKey(identifier)) ? preferences.getAll()
                .get(identifier) : null;

        if (returnValue != null) {
            return (T) returnValue;
        } else {
            return defaultValue;
        }
    }

    @SuppressWarnings("unchecked")
    @SuppressLint("ApplySharedPref")
    private <T> void writePreference(@NonNull String identifier, @NonNull T value, boolean commit) throws InvalidClassException {
        SharedPreferences.Editor preferencesEditor = getSharedPreferences().edit();

        if (value instanceof String) {
            preferencesEditor.putString(identifier, (String) value);
        } else if (value instanceof Long) {
            preferencesEditor.putLong(identifier, (Long) value);
        } else if (value instanceof Integer) {
            preferencesEditor.putInt(identifier, (Integer) value);
        } else if (value instanceof Float) {
            preferencesEditor.putFloat(identifier, (Float) value);
        } else if (value instanceof Boolean) {
            preferencesEditor.putBoolean(identifier, (Boolean) value);
        } else if (value instanceof Set) {
            preferencesEditor.putStringSet(identifier, (Set<String>) value);
        } else {
            throw new InvalidClassException("The value with type of " + Logger.generateLogTag(value) + " cannot be written to a preference library");
        }

        if (commit) {
            preferencesEditor.commit();
        } else {
            preferencesEditor.apply();
        }

        getPreferenceCache().put(identifier, value);
    }

    public boolean removePreference(@NonNull String identifier) {
        try {
            SharedPreferences preferences = getSharedPreferences();
            getPreferenceCache().remove(identifier);
            preferences.edit()
                    .remove(identifier)
                    .apply();
            return true;
        } catch (Exception e) {
            Logger.E(this, e);
            return false;
        }
    }

    public boolean clearPreferences() {
        try {
            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.clear();
            editor.apply();
            return true;
        } catch (Exception e) {
            Logger.E(this, e);
            return false;
        }
    }

    @NonNull
    public String getPreference(@NonNull String identifier, @NonNull String defaultValue) {
        return readPreference(identifier, defaultValue);
    }

    @NonNull
    public Long getPreference(@NonNull String identifier, @NonNull Long defaultValue) {
        return readPreference(identifier, defaultValue);
    }

    @NonNull
    public Integer getPreference(@NonNull String identifier, @NonNull Integer defaultValue) {
        return readPreference(identifier, defaultValue);
    }

    @NonNull
    public Float getPreference(@NonNull String identifier, @NonNull Float defaultValue) {
        return readPreference(identifier, defaultValue);
    }

    @NonNull
    public Boolean getPreference(@NonNull String identifier, @NonNull Boolean defaultValue) {
        return readPreference(identifier, defaultValue);
    }

    @NonNull
    public Set<String> getPreference(@NonNull String identifier, @NonNull Set<String> defaultValue) {
        return readPreference(identifier, defaultValue);
    }

    @NonNull
    public PreferenceLibrary applyPreference(@NonNull String identifier, @NonNull String value) {
        try {
            writePreference(identifier, value, false);
        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }
        return this;
    }

    @NonNull
    public PreferenceLibrary applyPreference(@NonNull String identifier, @NonNull Integer value) {
        try {
            writePreference(identifier, value, false);
        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }
        return this;
    }

    @NonNull
    public PreferenceLibrary applyPreference(@NonNull String identifier, @NonNull Long value) {
        try {
            writePreference(identifier, value, false);
        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }
        return this;
    }

    @NonNull
    public PreferenceLibrary applyPreference(@NonNull String identifier, @NonNull Float value) {
        try {
            writePreference(identifier, value, false);

        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }
        return this;
    }

    @NonNull
    public PreferenceLibrary applyPreference(@NonNull String identifier, @NonNull Boolean value) {
        try {
            writePreference(identifier, value, false);
        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }
        return this;
    }

    @NonNull
    public PreferenceLibrary applyPreference(@NonNull String identifier, @NonNull Set<String> value) {
        try {
            writePreference(identifier, value, false);

        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }
        return this;
    }

    @NonNull
    public PreferenceLibrary commitPreference(@NonNull String identifier, @NonNull String value) {
        try {
            writePreference(identifier, value, true);
        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }

        return this;
    }

    @NonNull
    public PreferenceLibrary commitPreference(@NonNull String identifier, @NonNull Integer value) {
        try {
            writePreference(identifier, value, true);
        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }

        return this;
    }

    @NonNull
    public PreferenceLibrary commitPreference(@NonNull String identifier, @NonNull Long value) {
        try {
            writePreference(identifier, value, true);
        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }

        return this;
    }

    @NonNull
    public PreferenceLibrary commitPreference(@NonNull String identifier, @NonNull Float value) {
        try {
            writePreference(identifier, value, true);
        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }

        return this;
    }

    @NonNull
    public PreferenceLibrary commitPreference(@NonNull String identifier, @NonNull Boolean value) {
        try {
            writePreference(identifier, value, true);
        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }

        return this;
    }

    @NonNull
    public PreferenceLibrary commitPreference(@NonNull String identifier, @NonNull Set<String> value) {
        try {
            writePreference(identifier, value, true);
        } catch (InvalidClassException e) {
            Logger.E(this, e);
        }

        return this;
    }

    public static class Builder {

        @NonNull
        PreferenceLibrary library = new PreferenceLibrary();

        public Builder() {

        }

        @NonNull
        public Builder setAccessMode(int accessMode) {
            this.library.setAccessMode(accessMode);
            return this;
        }

        @NonNull
        public Builder setIdentifier(@NonNull String identifier) {
            this.library.setLibraryIdentifier(identifier);
            return this;
        }

        @NonNull
        public PreferenceLibrary build() {
            this.library.setPreferenceCache(this.library.getSharedPreferences()
                    .getAll());
            return this.library;
        }
    }
}
