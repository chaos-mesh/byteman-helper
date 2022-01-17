## GCHelper

`StressHelper` is a [Byteman's User-Defined Rule Helper](https://downloads.jboss.org/byteman/4.0.17/byteman-programmers-guide.html#user-defined-rule-helpers), which used to trigger garbage collection in JVM.

### Example

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