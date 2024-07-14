#!/bin/zsh

./gradlew clean build

basePath=.
libPath=$basePath/build/libs/lib65816/shared
jvmArgs=-Djava.library.path=$libPath
classPath=$basePath/build/classes/java/main

#JAVA_HOME=/Library/Java/JavaVirtualMachines/arm64-zulu-8.jdk/Contents/Home

echo   libPath: "$libPath"
echo classPath: "$classPath"

$JAVA_HOME/bin/java $jvmArgs -cp $classPath emu.Main $*
 
