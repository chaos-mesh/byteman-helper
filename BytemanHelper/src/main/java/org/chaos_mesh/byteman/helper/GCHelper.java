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


public class GCHelper extends Helper
{
    protected GCHelper(Rule rule) {
        super(rule);
    }

    public static void installed(Rule rule)
    {
        System.out.println("byteman gc helper trigger gc");
        System.gc();
    }

    public void gc()
    {
        // do nothing
        return;
    }
}
