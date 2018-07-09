package com.peridotapps.nitro.hardware;

import android.os.Build;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public final class Cpu {
  
  private Cpu() {
  }
  
  private static final String CPU_FILTER_REGEX = "cpu[0-9]+";
  private static final String CPU_INFO_PATH = "/sys/devices/system/cpu/";
  private static final int CPU_DEFAULT_CORE_COUNT = 2;
  
  private static final AtomicInteger atomicCoreCount = new AtomicInteger(0);
  
  public static int getNumberOfProcessorCores() {
    int coreCount = getAtomicCoreCount();
    
    if (coreCount == 0) {
      try {
        if (Build.VERSION.SDK_INT >= 17) {
          coreCount = Runtime.getRuntime().availableProcessors();
        } else {
          coreCount = getLegacyNumberOfProcessorCores();
        }
      } catch (Exception e) {
        coreCount = CPU_DEFAULT_CORE_COUNT;
      }
      
      setAtomicCoreCount(coreCount);
    }
    
    return coreCount;
  }
  
  private static int getLegacyNumberOfProcessorCores() {
    File directory = new File(CPU_INFO_PATH);
    File[] files =
        directory.listFiles(pathname -> Pattern.matches(CPU_FILTER_REGEX, pathname.getName()));
    return files.length;
  }
  
  private static void setAtomicCoreCount(int count) {
    synchronized (atomicCoreCount) {
      atomicCoreCount.set(count);
    }
  }
  
  private static int getAtomicCoreCount() {
    synchronized (atomicCoreCount) {
      return atomicCoreCount.get();
    }
  }
}
