# byteman-helper

The repository maintains byteman [helpers](https://downloads.jboss.org/byteman/4.0.24/byteman-programmers-guide.html#user-defined-rule-helpers), plug-ins, scripts,
and anything else that extends byteman's functionality, which are used for Chaos Mesh.

## BytemanHelper

- SQLHelper

  [SQLHelper](./BytemanHelper/SQLHelper) is is used to parse SQL and judge whether this SQL match specified database and table.

- GCHelper

  [GCHelper](./BytemanHelper/GCHelper) is used to trigger garbage collection in JVM.

- StressHelper

  [StressHelper](./BytemanHelper/StressHelper) is used to inject CPU or memory stress into JVM.

## AgentInstaller

[AgentInstaller](./AgentInstaller) is used to install agent.

## ChaosAgent

[ChaosAgent](./ChaosAgent) is a agent which can be attach to JVM, it is used to provide the inject trigger point for JVMChaos(byteman).

## License

Licensed under the [Apache License 2.0](./LICENSE).
