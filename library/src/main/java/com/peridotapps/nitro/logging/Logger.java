package com.peridotapps.nitro.logging;

import android.support.annotation.StringDef;
import android.util.Log;

import com.peridotapps.nitro.concurrent.task.RunnableTask;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

public final class Logger {
    
    private Logger() {
    }
    
    public static final String DEFAULT_LOG_TAG = "<unknown>";
    
    public static String generateLogTag(Class<?> type) {
        return type.getSimpleName();
    }
    
    public static String generateLogTag(Object obj) {
        return generateLogTag(obj.getClass());
    }
    
    public static String generateLogTag(Throwable t) {
        if (t.getStackTrace().length > 1) {
            return t.getStackTrace()[1].getClassName();
        }
        return DEFAULT_LOG_TAG;
    }
    
    public static void E(Throwable t) {
        E(generateLogTag(t), t);
    }
    
    public static void E(String tag, Throwable t) {
        logMessage(tag, Log.getStackTraceString(t), LogType.EXCEPTION);
    }
    
    public static void E(Class<?> type, Throwable t) {
        E(generateLogTag(type), t);
    }
    
    public static void E(Object obj, Throwable t) {
        E(generateLogTag(obj), t);
    }
    
    public static void D(String message) {
        D(generateLogTag(new Throwable()), message);
    }
    
    public static void D(String tag, String message) {
        logMessage(tag, message, LogType.DEBUG);
    }
    
    public static void D(Class<?> type, String message) {
        D(generateLogTag(type), message);
    }
    
    public static void D(Object obj, String message) {
        D(generateLogTag(obj), message);
    }
    
    public static void I(String message) {
        I(generateLogTag(new Throwable()), message);
    }
    
    public static void I(String tag, String message) {
        logMessage(tag, message, LogType.INFORMATION);
    }
    
    public static void I(Class<?> type, String message) {
        I(generateLogTag(type), message);
    }
    
    public static void I(Object obj, String message) {
        I(generateLogTag(obj), message);
    }
    
    public static void W(String message) {
        W(generateLogTag(new Throwable()), message);
    }
    
    public static void W(String tag, String message) {
        logMessage(tag, message, LogType.WARNING);
    }
    
    public static void W(Class<?> type, String message) {
        W(generateLogTag(type), message);
    }
    
    public static void W(Object obj, String message) {
        W(generateLogTag(obj), message);
    }
    
    public static void V(String message) {
        V(generateLogTag(new Throwable()), message);
    }
    
    public static void V(String tag, String message) {
        logMessage(tag, message, LogType.VERBOSE);
    }
    
    public static void V(Class<?> type, String message) {
        V(generateLogTag(type), message);
    }
    
    public static void V(Object obj, String message) {
        V(generateLogTag(obj), message);
    }
    
    public static void WTF(String message) {
        WTF(generateLogTag(new Throwable()), message);
    }
    
    public static void WTF(String tag, String message) {
        logMessage(tag, message, LogType.WHAT_THE_F);
    }
    
    public static void WTF(Class<?> type, String message) {
        WTF(generateLogTag(type), message);
    }
    
    public static void WTF(Object obj, String message) {
        WTF(generateLogTag(obj), message);
    }
    
    private static void logMessage(String tag, String message, @LogType String logType) {
        getLogMessageRunnable(tag, message, logType).execute();
    }
    
    private static RunnableTask getLogMessageRunnable(String tag, String message, @LogType String logType) {
        return new RunnableTask() {
            @Override
            public void onRun() throws Exception {
                writeToLog(logType, tag, message);
            }
        };
    }
    
    private static void writeToLog(@LogType String logType, String tag, String message) {
        switch (logType.toLowerCase()) {
            case LogType.DEBUG:
                Log.d(tag, message);
                break;
            case LogType.EXCEPTION:
                Log.e(tag, message);
                break;
            case LogType.INFORMATION:
                Log.i(tag, message);
                break;
            case LogType.WARNING:
                Log.w(tag, message);
                break;
            case LogType.WHAT_THE_F:
                Log.wtf(tag, message);
                break;
            default:
                Log.v(tag, message);
                break;
        }
    }
    
    @Documented
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE})
    @StringDef({
        LogType.DEBUG,
        LogType.EXCEPTION,
        LogType.INFORMATION,
        LogType.VERBOSE,
        LogType.WARNING,
        LogType.WHAT_THE_F
    })
    public @interface LogType {
        String DEBUG = "d";
        String EXCEPTION = "e";
        String INFORMATION = "i";
        String VERBOSE = "v";
        String WARNING = "w";
        String WHAT_THE_F = "wtf";
    }
}
