#!/bin/zsh

./gradlew clean build generateRunScript
./build/runApp.sh config/turaco.cfg /Users/rebecca/Developer/Archive/calypsi-minimal-example/test.pgz
