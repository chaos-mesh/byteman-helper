## StressHelper

`StressHelper` is a [Byteman's User-Defined Rule Helper](https://downloads.jboss.org/byteman/4.0.17/byteman-programmers-guide.html#user-defined-rule-helpers), which used to inject CPU or memory stress into JVM.

| Type | Method | Description |
| ---- | ------ | ------------|
| void | injectCPUStress(String name, int cpuCount) | Inject CPU stress into the JVM. The `name` must be unique in one Java process, the `cpuCount` is the number of CPU which `StressHelper` used. |
| void | injectMemStress(String name, String memType) | Inject Memory stress into the JVM until out of memory. The `name` must be unique in one Java process, the `memType` is the memory type, it's value can be "heap" and "stack". |

### Example

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