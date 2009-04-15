rem TODO - Make this an Ant task!!!!
@echo off
rem * Original version by Doug Bullard; modified by Bill Jackson
rem * Utility for finding exactly where a class is in your classpath.
rem * Usage: WhenceJava [flags] <classToFind> <searchPath>
rem * Run without arguments for full usage text

setlocal
set LOCALCLASSPATH=%~dp0
rem For debugging:
rem set JAVA_ARGS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=50010

java %JAVA_ARGS% -cp %LOCALCLASSPATH%;%CLASSPATH%;unversioned\lib\build\buildtasks.jar net.nike.build.WhenceJava %*

endlocal