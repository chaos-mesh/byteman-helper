package org.chaos_mesh.byteman.helper;

import java.util.*;
import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Helper class used by ThreadPoolHelper script to create more thread in thread pool.
 */
public class ThreadPoolHelper extends Helper
{
    static int threadNum = 0;
    static ArrayList<ThreadTask> threadTasks = new ArrayList<ThreadTask>();
    static boolean hasException = false; 

    protected ThreadPoolHelper(Rule rule) {
        super(rule);
    }

    public static void uninstalled(Rule rule)
    {
        System.out.println("uninstalled thread pool fault injector");
        for (ThreadTask threadTask : threadTasks)
        {
            threadTask.setStop(true);
        }
        
        ThreadPoolHelper.threadTasks.clear();
        ThreadPoolHelper.threadNum = 0;
        ThreadPoolHelper.hasException = false;
    }

    public void traceExecute(ThreadPoolExecutor threadPool, int num)
    {
        if (ThreadPoolHelper.hasException)
        {
            return;
        }

        try {
            if (ThreadPoolHelper.threadNum > num) {
                return;
            }

            ThreadPoolHelper.threadNum++;
            ThreadTask task = new ThreadTask();
            threadTasks.add(task);
            task.setInterval(9999999);
            threadPool.execute(task);
        } catch(Exception e) {
            System.out.println("traceExecute get exception when execute new thread:" + e);
            ThreadPoolHelper.hasException = true;
            return;
        }
    }
}