#!/bin/bash
../gradlew clean build distZip
cd build/distributions
unzip Server-0.9.zip
cd Server-0.9
cd bin
./Server
