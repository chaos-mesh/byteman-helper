# byteman-helper

The repository maintains byteman [helpers](https://downloads.jboss.org/byteman/4.0.17/byteman-programmers-guide.html#user-defined-rule-helpers), plug-ins, scripts, and anything else that extends byteman's functionality.

## BytemanHelper

Some [Byteman's User-Defined Rule Helpers](https://downloads.jboss.org/byteman/4.0.17/byteman-programmers-guide.html#user-defined-rule-helpers).

### SQLHelper

[SQLHelper](./BytemanHelper/SQLHelper) is is used to parse SQL and judge whether this SQL match specified database and table.

### GCHelper

[GCHelper](./BytemanHelper/GCHelper) is used to trigger garbage collection in JVM.

### StressHelper

[StressHelper](./BytemanHelper/StressHelper) is used to inject CPU or memory stress into JVM.

## AgentInstaller

[AgentInstaller](./AgentInstaller) is used to install agent.

## ChaosAgent

[ChaosAgent](./ChaosAgent) is a agent which can be attach to JVM, it is used to provide the inject trigger point for JVMChaos(byteman).
