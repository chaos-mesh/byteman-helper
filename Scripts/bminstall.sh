#!/bin/bash
#
# Copyright 2022 Chaos Mesh Authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# See the License for the specific language governing permissions and
# limitations under the License.

#
# shell script which can be used to install the Byteman agent into
# a JVM which was started without the agent. This provides an
# alternative to using the -javaagent java command line flag
#
# usage: bminstall [-p port] [-h host] [-b] [-s] [-m] [-Dname[=value]]* pid
#   pid is the process id of the target JVM
#   -h host selects the host name or address the agent listener binds to
#   -p port selects the port the agent listener binds to
#   -b adds the byteman jar to the bootstrap classpath
#   -s sets an access-all-areas security policy for the Byteman agent code
#   -m activates the byteman JBoss modules plugin
#   -Dname=value can be used to set system properties whose name starts with "org.jboss.byteman."
#   expects to find a byteman agent jar and byteman JBoss modules plugin jar (if -m is indicated) in BYTEMAN_HOME
#

# helper function to obtain java version
function print_java_version()
{
    typeset -l java_version
    # grep output for line : 'java/openjdk version "AAAAAAAA"' where A is alpha, num, '''. or '-'
    java_version=`java -version 2>&1 |  grep "version" | cut -d'"' -f2`
    # format for JDK8- is 1.N.[n_]+
    if [ ${java_version%%.*} == 1 ] ; then
        echo $java_version | cut -d'.' -f2
    else
        # format may JDK9+ may be N.n.[n_]+ for proper
        # release or N-aaa for internal/ea build
        echo ${java_version%%[.-]*}
    fi
}

# use BYTEMAN_HOME to locate installed byteman release
if [ -z "$BYTEMAN_HOME" ]; then
# use the root of the path to this file to locate the byteman jar
    BYTEMAN_HOME="${0%*/bin/bminstall.sh}"
# allow for rename to plain bminstall
    if [ "$BYTEMAN_HOME" == "$0" ]; then
	BYTEMAN_HOME="${0%*/bin/bminstall}"
    fi
    if [ "$BYTEMAN_HOME" == "$0" ]; then
	echo "Unable to find byteman home"
	exit
    fi
fi

# check that we can find the byteman jar via BYTEMAN_HOME

# the Install class is in the byteman-install jar
if [ -r "${BYTEMAN_HOME}/lib/byteman.jar" ]; then
    BYTEMAN_JAR="${BYTEMAN_HOME}/lib/byteman.jar"
else
    echo "Cannot locate byteman jar"
    exit
fi
# the Install class is in the byteman-install jar
if [ -r "${BYTEMAN_HOME}/lib/byteman-install.jar" ]; then
    BYTEMAN_INSTALL_JAR="${BYTEMAN_HOME}/lib/byteman-install.jar"
else
    echo "Cannot locate byteman install jar"
    exit
fi

# chaos agent provides the inject trigger point for inject stress and gc
if [ -r "${BYTEMAN_HOME}/lib/chaos-agent.jar" ]; then
    CHAOS_AGENT_JAR="${BYTEMAN_HOME}/lib/chaos-agent.jar"
else
    echo "Cannot locate chaos agent jar"
    exit
fi

#  agent installer is used to install chaos agent
if [ -r "${BYTEMAN_HOME}/lib/agent-installer.jar" ]; then
    AGENT_INSTALLER_JAR="${BYTEMAN_HOME}/lib/agent-installer.jar"
else
    echo "Cannot locate agent installer jar"
    exit
fi

# for jdk6/7/8 we also need a tools jar from JAVA_HOME
JAVA_VERSION=$(print_java_version)
if [ $JAVA_VERSION -le 8 ]; then
  if [ -z "$JAVA_HOME" ]; then
    echo "please set JAVA_HOME"
    exit
fi

# on Linux we need to add the tools jar to the path
# this is not currently needed on a Mac
  OS=`uname`
  if [ ${OS} != "Darwin" ]; then
    if [ -r "${JAVA_HOME}/lib/tools.jar" ]; then
      TOOLS_JAR="${JAVA_HOME}/lib/tools.jar"
      CP="${BYTEMAN_INSTALL_JAR}:${TOOLS_JAR}"
    else
      echo "Cannot locate tools jar"
      CP="${BYTEMAN_INSTALL_JAR}"
    fi
  else
    if [ $JAVA_VERSION -gt 6 ]; then
      if [ -r "${JAVA_HOME}/Classes/classes.jar" ]; then
        TOOLS_JAR="${JAVA_HOME}/Classes/classes.jar"
        CP="${BYTEMAN_INSTALL_JAR}:${TOOLS_JAR}"
      else
        echo "Cannot locate tools jar"
        CP="${BYTEMAN_INSTALL_JAR}"
      fi
    else
      CP="${BYTEMAN_INSTALL_JAR}"
    fi
  fi
else
  CP="${BYTEMAN_INSTALL_JAR}"
fi

# allow for extra java opts via setting BYTEMAN_JAVA_OPTS
# attach class will validate arguments

USER=`echo "$USER"`
PID=${*: -1}
PID_USER="$( ps -o uname= -p "${PID}" )"
SUDO_PATH=`which sudo`

if [ "$USER" == "root" ] && [ "$PID_USER" != "root" ] && [ "$SUDO_PATH" != "" ]; then
  sudo -u $PID_USER JAVA_HOME=$JAVA_HOME BYTEMAN_HOME=$BYTEMAN_HOME $JAVA_HOME/bin/java ${BYTEMAN_JAVA_OPTS} -classpath "$CP" org.jboss.byteman.agent.install.Install $*
  if [ $JAVA_VERSION -le 8 ]; then
    sudo -u $PID_USER JAVA_HOME=$JAVA_HOME BYTEMAN_HOME=$BYTEMAN_HOME $JAVA_HOME/bin/java ${BYTEMAN_JAVA_OPTS} -classpath "$CP" org.jboss.byteman.agent.install.Install $*
    sudo -u $PID_USER JAVA_HOME=$JAVA_HOME BYTEMAN_HOME=$BYTEMAN_HOME $JAVA_HOME/bin/java -classpath "${AGENT_INSTALLER_JAR}:${CP}" org.chaos_mesh.agent_installer.Install -a ${CHAOS_AGENT_JAR} -p ${PID}
  else
    sudo -u $PID_USER BYTEMAN_HOME=$BYTEMAN_HOME java ${BYTEMAN_JAVA_OPTS} -classpath "$CP" org.jboss.byteman.agent.install.Install $*
    sudo -u $PID_USER BYTEMAN_HOME=$BYTEMAN_HOME java -classpath "${AGENT_INSTALLER_JAR}:${CP}" org.chaos_mesh.agent_installer.Install -a ${CHAOS_AGENT_JAR} -p ${PID}
  fi
else
  java ${BYTEMAN_JAVA_OPTS} -classpath "$CP" org.jboss.byteman.agent.install.Install $*
  java -classpath "${AGENT_INSTALLER_JAR}:${CP}" org.chaos_mesh.agent_installer.Install -a ${CHAOS_AGENT_JAR} -p ${PID}
fi

