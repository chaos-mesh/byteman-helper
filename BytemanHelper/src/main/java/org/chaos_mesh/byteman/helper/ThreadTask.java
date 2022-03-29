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
    private boolean stop = false;
    private ReentrantLock lock = new ReentrantLock();

    int interval;
    public ThreadTask() {
        
    }

    public void setStop(boolean stop) {
        this.lock.lock();
        this.stop = stop;
        this.lock.unlock();
    }

    public boolean getStop() {
        boolean stop = false;

        this.lock.lock();
        stop = this.stop;
        this.lock.unlock();

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
                    System.out.println("chaos thread: " + Thread.currentThread().getName() + " get exception when execute new chaos thread:" + e);
                }
            }
        }
    }
}