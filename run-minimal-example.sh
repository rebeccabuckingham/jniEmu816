#!/bin/zsh

# 12/17/2023 - Updated to remove hard dependency on java 8.
#
# note: make sure JAVA_HOME is set prior to running this.
#

./gradlew clean build

basePath=/Users/rebecca/Developer/jniEmu816
libPath=$basePath/build/libs/lib65816/shared
jvmArgs=-Djava.library.path=$libPath
classPath=$basePath/build/classes/java/main

echo   libPath: "$libPath"
echo classPath: "$classPath"

$JAVA_HOME/bin/java $jvmArgs -cp $classPath emu.Main /Users/rebecca/Developer/Archive/calypsi-minimal-example/test.pgz 

