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

import java.util.concurrent.locks.ReentrantLock;

public class ThreadTask implements Runnable{
    public static boolean stop = false;
    private static ReentrantLock lock = new ReentrantLock();

    int interval;
    public ThreadTask() {
        
    }

    public static void setStop(boolean stop) {
        ThreadTask.lock.lock();
        ThreadTask.stop = stop;
        ThreadTask.lock.unlock();
    }

    public static boolean getStop() {
        ThreadTask.lock.lock();
        stop = ThreadTask.stop;
        ThreadTask.lock.unlock();

        return stop;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
    
    public void run() {
        System.out.println("chaos thread: " + Thread.currentThread().getName());
        
        if (interval > 0) {
            for (int i = 0; i < interval; i++) {
                try {
                    Thread.sleep(1000);
                    if (this.getStop()) {
                        System.out.println("exit the chaos thread");
                        return;
                    }
                } catch(Exception e) {
                    System.out.println("get exception when execute new chaos thread:" + e);
                }         
            }
        }
    }
}