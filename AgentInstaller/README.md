## AgentInstaller

AgentInstaller is used to install agent.

Usage:

```bash
usage : Install [-a agent_jar] [-p pid]
        upload the agent into a running JVM
    -a the path of agent jar file
    -p pid is the process id of the target JVM or the unique name of the process as reported by the jps -l command
```

Example:

```bash
java  -classpath /Users/root/byteman-helper/AgentInstaller/target/AgentInstaller-1.0.jar org.chaos_mesh.agent_installer.Install -p 38444 -a "/Users/xiang/dev/byteman-helper/ChaosAgent/target/ChaosAgent-1.0.jar"
```