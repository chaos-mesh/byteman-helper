## ChaosAgent

ChaosAgent is a agent which can be attach to JVM, it is used to provide the inject trigger point for JVMChaos(byteman).

For example, when we want to trigger garbage collection, need to specified a `CLASS` and `METHOD` in byteman's rule file. ChaosAgent provide the trigger `CLASS`(`org.chaos_mesh.chaos_agent.TriggerThread`) and `METHOD`(`triggerFunc`), it will execute `triggerFunc` every 5 second.

The byteman's rule file looks like below:

```text
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