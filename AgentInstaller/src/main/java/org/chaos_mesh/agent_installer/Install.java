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

package org.chaos_mesh.agent_installer;

import com.sun.tools.attach.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;

import javax.swing.JButton;

/**
 * A program which uses the sun.com.tools.attach.VirtualMachine class to install the agent into a
 * running JVM. This provides an alternative to using the -javaagent option to install the agent.
 */
public class Install
{
    /**
     * main routine for use from command line
     *
     *
     * Install [-p pid] [-a agent_jar]
     *
     *
     * see method {@link #usage} for details of the command syntax
     * @param args the command options
     */
    public static void main(String[] args)
    {
        Install attachTest = new Install();
        attachTest.parseArgs(args);
        try {   
                attachTest.attach();
                attachTest.injectAgent();
                System.out.println("attach chaos agent success");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
       
    /**
     * compatability mode
     * @param pid the process id of the JVM into which the agent should be installed or 0 for this JVM
     * @param agentJar the path of agent jar file
     * @throws IllegalArgumentException if any of the arguments  is invalid
     * @throws FileNotFoundException if the agent jar cannot be found
     * @throws IOException if the agent jar cannot be opened or uploaded to the requested JVM
     * @throws AttachNotSupportedException if the requested JVM cannot be attached to
     * @throws AgentLoadException if an error occurs during upload of the agent into the JVM
     * @throws AgentInitializationException if the agent fails to initialize after loading. this almost always
     * indicates that the agent is already loaded into the JVM
     */
    public static void install(String pid, String agentJar)
              throws IllegalArgumentException, FileNotFoundException,
               IOException, AttachNotSupportedException,
               AgentLoadException, AgentInitializationException
    {
        
        Install install = new Install(pid, agentJar);
        install.attach();
        install.injectAgent();
    }

    public static VMInfo[] availableVMs()
    {
        List<VirtualMachineDescriptor> vmds = VirtualMachine.list();
        VMInfo[] vmInfo = new VMInfo[vmds.size()];
        int i = 0;
        for (VirtualMachineDescriptor vmd : vmds) {
            vmInfo[i++] = new VMInfo(vmd.id(), vmd.displayName());
        }

        return vmInfo;
    }

    /**
     * attach to the virtual machine identified by id and return the value of the named property. id must
     * be the id of a virtual machine returned by method availableVMs. 
     * @param id the id of the machine to attach to
     * @param property the proeprty to be retrieved
     * @return the value of the property or null if it is not set
     */
    public static String getSystemProperty(String id, String property)
    {
        return getProperty(id, property);
    }

    private static String getProperty(String id, String property)
    {
        VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach(id);
            String value = (String)vm.getSystemProperties().get(property);
            return value;
        } catch (AttachNotSupportedException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (vm != null) {
                try {
                    vm.detach();
                } catch (IOException e) {
                    // ignore;
                }
            }
        }
    }

    /**
     *  only this class creates instances
     */
    private Install()
    {
        agentJar = null;
        id = null;
        vm = null;
    }

    /**
     *  only this class creates instances
     */
    private Install(String pid, String agentJar)
    {
        this.agentJar = agentJar;
        this.id = pid;
        vm = null;
    }

    /**
     * check the supplied arguments and stash away the relevant data
     * @param args the value supplied to main
     */
    private void parseArgs(String[] args)
    {
        int argCount = args.length;
        int idx = 0;
        if (idx == argCount) {
            usage(0);
        }

        String nextArg = args[idx];

        while (nextArg.length() != 0 &&
                nextArg.charAt(0) == '-') {
            if (nextArg.equals("-p")) {
                idx++;
                if (idx == argCount) {
                    usage(1);
                }
                nextArg = args[idx];
                idx++;
                id = nextArg;
            } else if (nextArg.equals("-a")) {
                idx++;
                if (idx == argCount) {
                    usage(1);
                }
                nextArg = args[idx];
                idx++;
                agentJar = nextArg;
            } else if (nextArg.equals("--help")) {
                    usage(0);
            } else {
                System.out.println("Install : invalid option " + args[idx]);
                usage(1);
            }
            if (idx == argCount) {
                break;
            } else {
                nextArg = args[idx];
            }
        }
    }

    /**
     * attach to the Java process identified by the process id supplied on the command line
     */
    private void attach() throws AttachNotSupportedException, IOException, IllegalArgumentException
    {

        if (id.matches("[0-9]+")) {
            // integer process id
            int pid = Integer.valueOf(id);
            if (pid <= 0) {
                throw new IllegalArgumentException("Install : invalid pid " +id);
            }
            vm = VirtualMachine.attach(Integer.toString(pid));
        } else {
            // try to search for this VM with an exact match
            List<VirtualMachineDescriptor> vmds = VirtualMachine.list();
            for (VirtualMachineDescriptor vmd: vmds) {
                String displayName = vmd.displayName();
                int spacePos = displayName.indexOf(' ');
                if (spacePos > 0) {
                    displayName = displayName.substring(0, spacePos);
                }
                if (displayName.equals(id)) {
                    String pid = vmd.id();
                    vm = VirtualMachine.attach(vmd);
                    return;
                }
            }
            // hmm, ok, lets see if we can find a trailing match e.g. if the displayName
            // is org.jboss.Main we will accept jboss.Main or Main
            for (VirtualMachineDescriptor vmd: vmds) {
                String displayName = vmd.displayName();
                int spacePos = displayName.indexOf(' ');
                if (spacePos > 0) {
                    displayName = displayName.substring(0, spacePos);
                }

                if (displayName.indexOf('.') >= 0 && displayName.endsWith(id)) {
                    // looking hopeful ensure the preceding char is a '.'
                    int idx = displayName.length() - (id.length() + 1);
                    if (displayName.charAt(idx) == '.') {
                        // yes it's a match
                        String pid = vmd.id();
                        vm = VirtualMachine.attach(vmd);
                        return;
                    }
                }
            }

            // no match so throw an exception

            throw new IllegalArgumentException("Install : invalid pid " + id);
        }


    }

    /**
     * get the attached process to upload and install the agent jar using whatever agent options were
     * configured on the command line
     */
    private void injectAgent() throws AgentLoadException, AgentInitializationException, IOException
    {
        try {
            vm.loadAgent(agentJar);
        } finally {
            vm.detach();
        }
    }

    /**
     * print usage information and exit with a specific exit code
     * @param exitValue the value to be supplied to the exit call
     */
    private static void usage(int exitValue)
    {
        System.out.println("usage : Install [-a agent_jar] [-p pid]");
        System.out.println("        upload the agent into a running JVM");
        System.out.println("    -a the path of agent jar file");
        System.out.println("    -p pid is the process id of the target JVM or the unique name of the process as reported by the jps -l command");
        System.exit(exitValue);
    }

    private String agentJar;
    private String id;
    private VirtualMachine vm;
}
