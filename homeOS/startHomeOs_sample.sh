#!/usr/bin/env bash
java -Dlogback.configurationFile=config/logback.xml -cp .:lib/common.jar:lib/homeos-core.jar:lib/libsim.jar:lib/nrjavaserial.jar de/smahoo/kernel/HomeOs config-sample.xml