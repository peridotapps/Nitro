package com.peridotapps.nitro.logging;

import android.support.annotation.NonNull;
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
  
  private Logger () {
  }
  
  public static final String DEFAULT_LOG_TAG = "<unknown>";
  
  public static String generateLogTag (@NonNull Class<?> type) {
    return type.getSimpleName();
  }
  
  public static String generateLogTag (@NonNull Object obj) {
    return generateLogTag(obj.getClass());
  }
  
  public static String generateLogTag (@NonNull Throwable t) {
    if (t.getStackTrace().length > 1) {
      return t.getStackTrace()[1].getClassName();
    }
    return DEFAULT_LOG_TAG;
  }
  
  public static void E (@NonNull Throwable t) {
    E(generateLogTag(t), t);
  }
  
  public static void E (@NonNull String tag, @NonNull Throwable t) {
    logMessage(tag, Log.getStackTraceString(t), LogType.EXCEPTION);
  }
  
  public static void E (@NonNull Class<?> type, @NonNull Throwable t) {
    E(generateLogTag(type), t);
  }
  
  public static void E (@NonNull Object obj, @NonNull Throwable t) {
    E(generateLogTag(obj), t);
  }
  
  public static void D (@NonNull String message) {
    D(generateLogTag(new Throwable()), message);
  }
  
  public static void D (@NonNull String tag, @NonNull String message) {
    logMessage(tag, message, LogType.DEBUG);
  }
  
  public static void D (@NonNull Class<?> type, @NonNull String message) {
    D(generateLogTag(type), message);
  }
  
  public static void D (@NonNull Object obj, @NonNull String message) {
    D(generateLogTag(obj), message);
  }
  
  public static void I (@NonNull String message) {
    I(generateLogTag(new Throwable()), message);
  }
  
  public static void I (@NonNull String tag, @NonNull String message) {
    logMessage(tag, message, LogType.INFORMATION);
  }
  
  public static void I (@NonNull Class<?> type, @NonNull String message) {
    I(generateLogTag(type), message);
  }
  
  public static void I (@NonNull Object obj, @NonNull String message) {
    I(generateLogTag(obj), message);
  }
  
  public static void W (@NonNull String message) {
    W(generateLogTag(new Throwable()), message);
  }
  
  public static void W (@NonNull String tag, @NonNull String message) {
    logMessage(tag, message, LogType.WARNING);
  }
  
  public static void W (@NonNull Class<?> type, @NonNull String message) {
    W(generateLogTag(type), message);
  }
  
  public static void W (@NonNull Object obj, @NonNull String message) {
    W(generateLogTag(obj), message);
  }
  
  public static void V (@NonNull String message) {
    V(generateLogTag(new Throwable()), message);
  }
  
  public static void V (@NonNull String tag, @NonNull String message) {
    logMessage(tag, message, LogType.VERBOSE);
  }
  
  public static void V (@NonNull Class<?> type, @NonNull String message) {
    V(generateLogTag(type), message);
  }
  
  public static void V (@NonNull Object obj, @NonNull String message) {
    V(generateLogTag(obj), message);
  }
  
  public static void WTF (@NonNull String message) {
    WTF(generateLogTag(new Throwable()), message);
  }
  
  public static void WTF (@NonNull String tag, @NonNull String message) {
    logMessage(tag, message, LogType.WHAT_THE_F);
  }
  
  public static void WTF (@NonNull Class<?> type, @NonNull String message) {
    WTF(generateLogTag(type), message);
  }
  
  public static void WTF (@NonNull Object obj, @NonNull String message) {
    WTF(generateLogTag(obj), message);
  }
  
  private static void logMessage (@NonNull String tag, @NonNull String message, @NonNull @LogType String logType) {
    getLogMessageRunnable(tag, message, logType).execute();
  }
  
  private static RunnableTask getLogMessageRunnable (@NonNull String tag, @NonNull String message, @NonNull @LogType String logType) {
    return new RunnableTask() {
      @Override
      public void onRun () throws Exception {
        writeToLog(logType, tag, message);
      }
    };
  }
  
  private static void writeToLog (@LogType @NonNull String logType, @NonNull String tag, @NonNull String message) {
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
  @StringDef({LogType.DEBUG, LogType.EXCEPTION, LogType.INFORMATION, LogType.VERBOSE, LogType.WARNING, LogType.WHAT_THE_F})
  public @interface LogType {
    String DEBUG = "d";
    String EXCEPTION = "e";
    String INFORMATION = "i";
    String VERBOSE = "v";
    String WARNING = "w";
    String WHAT_THE_F = "wtf";
  }
}
