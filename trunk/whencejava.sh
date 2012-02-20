#!/bin/bash
#todo make this an Ant task!
#OS X
#set JAVA_HOME=/usr
#solaris
JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home/
#set PATH=$PATH:$JAVA_HOME/bin
export PATH
export JAVA_HOME
export ANT_OPTS=-Xmx640m

set CLASSPATH=.:unversioned/lib/build/buildtasks.jar
java  com.nurflugel.buildtasks.WhenceJava $*
