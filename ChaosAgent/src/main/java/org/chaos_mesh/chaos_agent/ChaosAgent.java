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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * ChaosAgent class supplied at JVM startup to install agent
 */
public class ChaosAgent {
    public static boolean firstTime = true;

    public static void premain(String args, Instrumentation inst)
            throws Exception
    {
        // guard against the agent being loaded twice
        synchronized (ChaosAgent.class) {
            if (firstTime) {
                firstTime = false;
            } else {
                throw new Exception("Main : attempting to load chaos agent more than once");
            }
        }
        
        TriggerThread thread = new TriggerThread();
        thread.start();
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception
    {
        premain(args, inst);
    }
}
