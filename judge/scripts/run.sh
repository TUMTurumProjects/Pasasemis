#!/usr/bin/env bash

cd $1
mvn -e clean compile exec:java -Dexec.mainClass=Runner -Dexec.args="$2"
cd -

