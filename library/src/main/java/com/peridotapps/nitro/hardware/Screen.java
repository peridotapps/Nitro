package com.peridotapps.nitro.hardware;

import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicReference;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

public final class Screen {

    private Screen() {
    }

    @NonNull
    private static final AtomicReference<DisplayMetrics> atomicDisplayMetrics = new AtomicReference<>(initDisplayMetrics());

    @NonNull
    private static final AtomicReference<Double> atomicDisplaySizeInches = new AtomicReference<>(0.0D);

    @NonNull
    private static DisplayMetrics initDisplayMetrics() {
        return Resources.getSystem()
                .getDisplayMetrics();
    }

    @NonNull
    private static DisplayMetrics getDisplayMetrics() {
        synchronized (atomicDisplayMetrics) {
            return atomicDisplayMetrics.get();
        }
    }

    private static double getDisplaySizeInInchesAtomic() {
        synchronized (atomicDisplaySizeInches) {
            return atomicDisplaySizeInches.get();
        }
    }

    private static void setDisplaySizeInchesAtomic(double size) {
        synchronized (atomicDisplaySizeInches) {
            atomicDisplaySizeInches.set(size);
        }
    }

    public static double getDeviceScreenSizeInInches() {
        return getDeviceScreenSizeInInches(getDisplayMetrics());
    }

    public static double getDeviceDisplayHeightInches() {
        return getDeviceDisplayHeightInches(getDisplayMetrics());
    }

    public static double getDeviceDisplayWidthInches() {
        return getDeviceDisplayWidthInches(getDisplayMetrics());
    }

    public static double getDeviceDisplayHeightPixels() {
        return getDeviceDisplayHeightPixels(getDisplayMetrics());
    }

    public static double getDeviceDisplayWidthPixels() {
        return getDeviceDisplayWidthPixels(getDisplayMetrics());
    }

    @ScreenOrientation
    public static int getDeviceOrientation() {
        return Resources.getSystem()
                .getConfiguration().orientation;
    }

    @NonNull
    public static ScreenDimension getScreenDimensions() {
        DisplayMetrics dm = getDisplayMetrics();

        return new ScreenDimension(getDeviceScreenSizeInInches(dm), getDeviceDisplayWidthInches(dm), getDeviceDisplayHeightInches(dm), getDeviceDisplayWidthPixels(dm), getDeviceDisplayHeightPixels(dm), getDeviceOrientation(), dm.densityDpi);
    }

    private static double getDeviceScreenSizeInInches(DisplayMetrics dm) {
        double size = getDisplaySizeInInchesAtomic();

        if (size <= 0.0D) {
            size = Math.sqrt(Math.pow(getDeviceDisplayWidthInches(dm), 2) + Math.pow(getDeviceDisplayHeightInches(dm), 2));
            setDisplaySizeInchesAtomic(size);
        }

        return size;
    }

    private static double getDeviceDisplayHeightInches(DisplayMetrics dm) {
        return getInchesFromPixelsAndDensity(getDeviceDisplayHeightPixels(dm), dm);
    }

    private static double getDeviceDisplayWidthInches(DisplayMetrics dm) {
        return getInchesFromPixelsAndDensity(getDeviceDisplayWidthPixels(dm), dm);
    }

    private static int getDeviceDisplayHeightPixels(DisplayMetrics dm) {
        return dm.heightPixels;
    }

    private static int getDeviceDisplayWidthPixels(DisplayMetrics dm) {
        return dm.widthPixels;
    }

    private static double getInchesFromPixelsAndDensity(int pixelCount, DisplayMetrics dm) {
        return (double) pixelCount / (double) dm.densityDpi;
    }

    @Documented
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE})
    @IntDef({ORIENTATION_LANDSCAPE, ORIENTATION_PORTRAIT})
    public @interface ScreenOrientation {
    }

    public static final class ScreenDimension {

        private double screenSizeInches;
        private double widthInches;
        private double heightInches;
        private double widthPixels;
        private double heightPixels;
        @ScreenOrientation
        private int orientation;
        private int density;

        private ScreenDimension(double screenSizeInches, double widthInches, double heightInches, double widthPixels, double heightPixels, @ScreenOrientation int orientation, int density) {
            this.screenSizeInches = screenSizeInches;
            this.widthInches = widthInches;
            this.heightInches = heightInches;
            this.widthPixels = widthPixels;
            this.heightPixels = heightPixels;
            this.orientation = orientation;
            this.density = density;
        }

        public double getScreenSizeInches() {
            return screenSizeInches;
        }

        public double getWidthInches() {
            return widthInches;
        }

        public double getHeightInches() {
            return heightInches;
        }

        public double getWidthPixels() {
            return widthPixels;
        }

        public double getHeightPixels() {
            return heightPixels;
        }

        @ScreenOrientation
        public int getOrientation() {
            return orientation;
        }

        public int getDensity() {
            return density;
        }
    }
}
