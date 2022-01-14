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
import org.jboss.byteman.rule.Action;

import java.nio.ByteBuffer;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StressHelper extends Helper
{
    static HashMap<String, Stress> stresses = new HashMap<String, Stress>();

    protected StressHelper(Rule rule) {
        super(rule);
    }

    public static void uninstalled(Rule rule)
    {
        Stress stress = stresses.get(rule.getName());
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
        threads = new ArrayList<CPUStressThread>();
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
    private MemoryStressThread thread;

    MemoryStress(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public void load() {
        thread = new MemoryStressThread(name, type);
        thread.start();
    }

    public void quit() {
        thread.shutdown();
    }
}

interface StressRunnable extends Runnable {

    public void shutdown();
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

    private ReentrantLock lock = new ReentrantLock();

    MemoryStressThread(String name, String type) {
        threadName = name;
        this.type = type;
        flag = true;
        Helper.verbose("Creating thread " +  threadName + ", type " + type);
    }

    public void run() {
        Helper.verbose("Running thread " +  threadName );
        ArrayList<String> increaseSizeData = new ArrayList<String>();
        boolean oom = false;

        while (true) {
            lock.lock();
            boolean exit = !flag;
            lock.unlock();
            if (exit) {
                increaseSizeData = null;
                ThreadTask.setStop(true);
                System.gc(); 
                break;
            }

            if (oom) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    Helper.verbose("exception: " + e);
                }
            } else {
                if (this.type.equals("heap")) {
                    try {
                        increaseSizeData.add("123456");
                    } catch (OutOfMemoryError e) {
                        oom = true;
                        Helper.verbose("exception: " + e);
                    }
                } else if (this.type.equals("stack")) {
                    try {
                        ThreadTask task = new ThreadTask();
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
