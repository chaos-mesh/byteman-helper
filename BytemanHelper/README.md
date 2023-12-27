## BytemanHelper

Some [Byteman's User-Defined Rule Helpers](https://downloads.jboss.org/byteman/4.0.22/byteman-programmers-guide.html#user-defined-rule-helpers).

### StressHelper

`StressHelper` is a [Byteman's User-Defined Rule Helper](https://downloads.jboss.org/byteman/4.0.22/byteman-programmers-guide.html#user-defined-rule-helpers), which is used to inject CPU or memory stress into JVM.

| Type | Method | Description |
| ---- | ------ | ------------|
| void | injectCPUStress(String name, int cpuCount) | Inject CPU stress into the JVM. The `name` must be unique in one Java process, the `cpuCount` is the number of CPUs that `StressHelper` used. |
| void | injectMemStress(String name, String memType) | Inject Memory stress into the JVM until out of memory. The `name` must be unique in one Java process, the `memType` is the memory type, its value can be "heap" and "stack". |

#### Example

```txt
RULE cpu test
CLASS org.chaos_mesh.chaos_agent.TriggerThread
METHOD triggerFunc
HELPER org.chaos_mesh.byteman.helper.StressHelper
AT ENTRY
IF true
DO
    injectCPUStress("cpu test", 2);
ENDRULE
```

### SQLHelper

`SQLHelper` is a [Byteman's User-Defined Rule Helper](https://downloads.jboss.org/byteman/4.0.22/byteman-programmers-guide.html#user-defined-rule-helpers), which is used to parse SQL and judge whether this SQL match specified database and table.

| Type | Method | Description |
| ---- | ------ | ------------|
| boolean | matchDBTable(String sql, String filterDatabase, String filterTable) | parse the `sql`, and judge whether this SQL match specified `filterDatabase` and `filterTable` |

#### Example

```txt
RULE mysql test
CLASS com.mysql.cj.jdbc.StatementImpl
METHOD executeQuery
HELPER org.chaos_mesh.byteman.helper.SQLHelper
AT ENTRY
BIND
     flag:boolean=matchDBTable($1, "test", "t1");
IF flag
DO
        throw new java.sql.SQLException("BOOM");
ENDRULE
```

SQLs match database `test` and table `t1` will get an exception when executing the query.

#### Build

```bash
mvn -X package -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true
```

### GCHelper

`GCHelper` is a [Byteman's User-Defined Rule Helper](https://downloads.jboss.org/byteman/4.0.22/byteman-programmers-guide.html#user-defined-rule-helpers), which used to trigger garbage collection in JVM.

#### Example

```txt
RULE gc test
CLASS org.chaos_mesh.chaos_agent.TriggerThread
METHOD triggerFunc
HELPER org.chaos_mesh.byteman.helper.GCHelper
AT ENTRY
IF true
DO
    gc();
ENDRULE
```

### ThreadPoolHelper

`ThreadPoolHelper` is a [Byteman's User-Defined Rule Helper](https://downloads.jboss.org/byteman/4.0.22/byteman-programmers-guide.html#user-defined-rule-helpers), it hijacks the `java.util.concurrent.ThreadPoolExecutor` and uses it to create the specified number of threads, preventing the user from creating threads.

| Type | Method | Description |
| ---- | ------ | ------------|
| void | threadPoolExecute(ThreadPoolExecutor threadPool, int num) | Using the `threadPool` to create the specified number of threads. |

#### Example

```txt
RULE apply thread pool
CLASS java.util.concurrent.ThreadPoolExecutor
METHOD execute
HELPER org.chaos_mesh.byteman.helper.ThreadPoolHelper
AT ENTRY
IF true
DO
    threadPoolExecute($0, 1000)
ENDRULE
```
