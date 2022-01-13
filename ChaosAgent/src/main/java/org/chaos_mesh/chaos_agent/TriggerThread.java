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

package org.chaos_mesh.chaos_agent;

public class TriggerThread extends Thread {
    public void run(){
        loop();
    }

    public static void loop() {
        while (true)
        {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            triggerFunc();
        }
    }

    public static void triggerFunc()
    {
        //System.out.println("chaos agent triger function");
    }
 }