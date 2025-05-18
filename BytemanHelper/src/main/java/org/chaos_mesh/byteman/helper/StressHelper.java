// Copyright 2022 Chaos Mesh Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// See the License for the specific language governing permissions and
// limitations under the License.
package org.chaos_mesh.byteman.helper;

import java.util.*;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

import java.util.concurrent.locks.ReentrantLock;


public class StressHelper extends Helper
{
    static HashMap<String, Stress> stresses = new HashMap<>();

    protected StressHelper(Rule rule) {
        super(rule);
    }

    public static void uninstalled(Rule rule)
    {
        Stress stress = stresses.get(rule.getName());
        if (stress == null)
        {
            Helper.verbose("Stress reference not found for rule " + rule.getName() + ". All keys: " + stresses.keySet());
            return;
        }
        stress.quit();
        stresses.remove(rule.getName());
    }

    public void injectCPUStress(String name, int cpuCount)
    {
        Stress stress = stresses.get(name);
        if (stress != null)
        {
            return;
        }

        stress = new CPUStress(name, cpuCount);
        stresses.put(name, stress);
        stress.load();
    }

    public void injectMemStress(String name, String memType)
    {
        Stress stress = stresses.get(name);
        if (stress != null)
        {
            return;
        }

        stress = new MemoryStress(name, memType);
        stresses.put(name, stress);
        stress.load();
    }

  public void injectLimitedMemStress(String name, String memType, String heapMemoryUsage)
  {
      Stress stress = stresses.get(name);
      if (stress != null)
      {
        return;
      }

      stress = new MemoryStress(name, memType, heapMemoryUsage);
      stresses.put(name, stress);
      stress.load();
  }

}

/*
    Stress is an interface used for inject stress on Java process, include Memory and CPU.
 */
interface Stress {
    // load the stress
    public void load();

    // quit stops the stress load
    public void quit();
}

class CPUStress implements Stress {
    private String name;
    private int cpuCount;
    private ArrayList<CPUStressThread> threads;

    CPUStress(String name, int cpuCount) {
        this.name = name;
        this.cpuCount = cpuCount;
        threads = new ArrayList<>();
    }

    public void load() {
        for (int i = 0; i < cpuCount; i++) {
            CPUStressThread thread = new CPUStressThread(name + i);
            threads.add(thread);
            thread.start();
        }
    }

    public void quit() {
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).shutdown();
        }
    }
}

class MemoryStress implements Stress {
    private String name;
    private String type;
    private String heapStressPercentage;
    private MemoryStressThread thread;

    MemoryStress(String name, String type) {
        this(name, type, null);
    }

    MemoryStress(String name, String type, String heapStressPercentage) {
        this.name = name;
        this.type = type;
        this.heapStressPercentage = heapStressPercentage;
    }

    public void load() {
        Long heapMemoryToBeUsedUp = getMaxHeapUsageRequested();
        thread = new MemoryStressThread(name, type, heapMemoryToBeUsedUp);
        thread.start();
    }

    private Long getMaxHeapUsageRequested() {
        if (heapStressPercentage == null) {
          return null;
        }

        Double heapStressPercentageDouble = null;
        Long heapMemoryToBeUsedUp = null;
        long maxMemory = Runtime.getRuntime().maxMemory();
        if (heapStressPercentage.endsWith("%")) {
            heapStressPercentageDouble = Double.parseDouble(heapStressPercentage.substring(0, heapStressPercentage.length() - 1));
        } else if (heapStressPercentage.toLowerCase().endsWith("mb") || heapStressPercentage.toLowerCase().endsWith("gb")) {
            String unit = heapStressPercentage.substring(heapStressPercentage.length() - 2).toLowerCase();
            double value = Double.parseDouble(heapStressPercentage.substring(0, heapStressPercentage.length() - 2));
            if (unit.equals("mb")) {
                heapMemoryToBeUsedUp = (long)(value * 1024 * 1024);
            } else if (unit.equals("gb")) {
                heapMemoryToBeUsedUp = (long)(value * 1024 * 1024 * 1024);
            }
        } else if (heapStressPercentage.contains(".")) {
            try {
                heapStressPercentageDouble = Double.parseDouble(heapStressPercentage);
            } catch (NumberFormatException e) {
                Helper.verbose("Failed to parse heapStressPercentage: " + heapStressPercentage);
            }
        } else {
            Helper.verbose("Failed to parse heapStressPercentage: " + heapStressPercentage + ". Some examples that should work: 50%, 100MB, 1GB, 0.5");
        }
        if (heapMemoryToBeUsedUp == null && heapStressPercentageDouble != null) {
            heapMemoryToBeUsedUp = (long)(maxMemory * heapStressPercentageDouble);
        }

        return heapMemoryToBeUsedUp;
    }

    public void quit() {
        thread.shutdown();
    }
}

interface StressRunnable extends Runnable {
}

class CPUStressThread implements StressRunnable {
    private Thread t;
    private String threadName;
    private boolean flag;

    private ReentrantLock lock = new ReentrantLock();

    CPUStressThread( String name ) {
        threadName = name;
        flag = true;
        Helper.verbose("Creating thread " +  threadName );
    }

    public void run() {
        Helper.verbose("Running thread " +  threadName );

        while (true) {
            lock.lock();
            boolean exit = !flag;
            lock.unlock();
            if (exit) {
                break;
            }
        }

        Helper.verbose("Exiting thread " +  threadName );
    }

    public void start() {
        Helper.verbose("Starting thread " +  threadName );

        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
   }

    public void shutdown() {
        Helper.verbose("Shutdown thread " +  threadName );
        lock.lock();
        flag = false;
        lock.unlock();
    }
}

class MemoryStressThread implements StressRunnable {
    private Thread t;
    private String threadName;
    private boolean flag;
    private String type;
    private Long heapMemoryToBeUsedUp;

    private ReentrantLock lock = new ReentrantLock();

    MemoryStressThread(String name, String type, Long heapMemoryToBeUsedUp) {
        threadName = name;
        this.type = type;
        this.heapMemoryToBeUsedUp = heapMemoryToBeUsedUp;
        flag = true;
        Helper.verbose("Creating thread " +  threadName + ", type " + type + ", heapMemoryToBeUsedUp " + heapMemoryToBeUsedUp + ", maxHeapSize " + Runtime.getRuntime().maxMemory());
    }

    MemoryStressThread(String name, String type) {
        this(name, type, null);
    }

    public void run() {
        Helper.verbose("Running thread " +  threadName );
        List<String> increaseSizeData = new LinkedList<>();
        ArrayList<ThreadTask> threadTasks = new ArrayList<>();
        boolean oom = false;

        logMemoryStats();
        long iteration = 0;
        long logIntervalIteration = 100000;
        while (true) {
            iteration++;
            lock.lock();
            boolean exit = !flag;
            lock.unlock();
            if (exit) {
                Helper.verbose("stop memory stress thread, exit condition reached");
                increaseSizeData = null;
                for (int i = 0; i < threadTasks.size(); i++) {
                    threadTasks.get(i).setStop(true);
                }
                System.gc();
                break;
            }

            if (oom) {
                Helper.verbose("oom condition reached, pause a little - 500ms");
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    Helper.verbose("exception: " + e);
                }
            } else {
                if (this.type.equals("heap")) {
                    if (shouldAddMoreHeapMemoryLoad(heapMemoryToBeUsedUp)) {
                        logMemoryStatsIfIntervalReached(iteration, logIntervalIteration);
                        try {
                          increaseSizeData.add("123456");
                        } catch (OutOfMemoryError e) {
                          oom = true;
                          Helper.verbose("exception: " + e);
                        }
                    } else {
                        Helper.verbose("heap memory fill condition reached, pause a little - 5000ms");
                        logMemoryStats();
                        try {
                          Thread.sleep(5000);
                        } catch (Exception e) {
                          Helper.verbose("exception: " + e);
                        }
                    }
                } else if (this.type.equals("stack")) {
                    try {
                        ThreadTask task = new ThreadTask();
                        threadTasks.add(task);
                        task.setInterval(9999999);
                        Thread th = new Thread(task);
                        th.start();
                    } catch (OutOfMemoryError e) {
                        oom = true;
                        Helper.verbose("exception: " + e);
                    }
                }
            }
        }

        Helper.verbose("Exiting thread " +  threadName );
    }

  private void logMemoryStats() {
      logMemoryStatsIfIntervalReached(1, 1);
  }

  private void logMemoryStatsIfIntervalReached(long iteration, long logIntervalIteration) {
      if (iteration % logIntervalIteration == 0) {
          long maxHeapSize = Runtime.getRuntime().maxMemory();
          long freeHeapSize = Runtime.getRuntime().freeMemory();
          long totalHeapSize = Runtime.getRuntime().totalMemory();
          Helper.verbose("heap memory stats: maxHeapSize " + maxHeapSize + ", freeHeapSize " + freeHeapSize + ", totalHeapSize " + totalHeapSize + ", heapMemoryToBeUsedUp " + heapMemoryToBeUsedUp + "; iteration " + iteration);
      }
  }

  boolean shouldAddMoreHeapMemoryLoad(Long heapMemoryToBeUsedUp) {
    if (heapMemoryToBeUsedUp == null || heapMemoryToBeUsedUp < 0) {
        return true;
    }

    long currentHeapSize = Runtime.getRuntime().totalMemory();
    long freeHeapSize = Runtime.getRuntime().freeMemory();

    return heapMemoryToBeUsedUp > (currentHeapSize - freeHeapSize);
  }

  public void start () {
        Helper.verbose("Starting thread " +  threadName );

        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
   }

    public void shutdown() {
        Helper.verbose("Shutdown thread " +  threadName );
        lock.lock();
        flag = false;
        lock.unlock();
    }
}
