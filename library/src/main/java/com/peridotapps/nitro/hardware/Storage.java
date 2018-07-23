package com.peridotapps.nitro.hardware;

import android.os.Environment;
import java.io.File;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class Storage {
  
  private static final AtomicReference<File> internalFileAtomicReference = new AtomicReference<>(initRootDirectoryFile());
  private static final AtomicReference<File> externalFileAtomicReference = new AtomicReference<>(initExternalDirectoryFile());
  private static final AtomicLong atomicInternalTotalSpace = new AtomicLong(-1L);
  private static final AtomicLong atomicExternalTotalSpace = new AtomicLong(-1L);
  
  private Storage () {
  }
  
  public static long getInternalTotalSpace () {
    long totalSpace = getAtomicInternalTotalSpace();
    
    if (totalSpace <= 0L) {
      totalSpace = getInternalStorageFile().getTotalSpace();
      setAtomicInternalTotalSpace(totalSpace);
    }
    
    return totalSpace;
  }
  
  public static long getInternalFreeSpace () {
    return getInternalStorageFile().getFreeSpace();
  }
  
  public static long getInternalUsedSpace () {
    return getInternalTotalSpace() - getInternalFreeSpace();
  }
  
  public static long getExternalTotalSpace () {
    long totalSpace = getAtomicExternalTotalSpace();
    
    if (totalSpace <= 0L) {
      totalSpace = getExternalStorageFile().getTotalSpace();
      setAtomicExternalTotalSpace(totalSpace);
    }
    
    return totalSpace;
  }
  
  public static long getExternalFreeSpace () {
    return getExternalStorageFile().getFreeSpace();
  }
  
  public static long getExternalUsedSpace () {
    return getExternalTotalSpace() - getExternalFreeSpace();
  }
  
  private static void setAtomicInternalTotalSpace (long totalSpace) {
    synchronized (atomicInternalTotalSpace) {
      atomicInternalTotalSpace.set(totalSpace);
    }
  }
  
  private static long getAtomicInternalTotalSpace () {
    synchronized (atomicInternalTotalSpace) {
      return atomicInternalTotalSpace.get();
    }
  }
  
  private static void setAtomicExternalTotalSpace (long totalSpace) {
    synchronized (atomicExternalTotalSpace) {
      atomicExternalTotalSpace.set(totalSpace);
    }
  }
  
  private static long getAtomicExternalTotalSpace () {
    synchronized (atomicExternalTotalSpace) {
      return atomicExternalTotalSpace.get();
    }
  }
  
  private static File getInternalStorageFile () {
    synchronized (internalFileAtomicReference) {
      if (internalFileAtomicReference.get() == null) {
        internalFileAtomicReference.set(initRootDirectoryFile());
      }
    }
    
    return internalFileAtomicReference.get();
  }
  
  private static File getExternalStorageFile () {
    synchronized (externalFileAtomicReference) {
      if (externalFileAtomicReference.get() == null) {
        externalFileAtomicReference.set(initExternalDirectoryFile());
      }
    }
    
    return externalFileAtomicReference.get();
  }
  
  private static File initRootDirectoryFile () {
    return Environment.getRootDirectory();
  }
  
  private static File initExternalDirectoryFile () {
    return Environment.getExternalStorageDirectory();
  }
  
}
