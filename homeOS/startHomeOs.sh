#!/usr/bin/env bash
java -Dlogback.configurationFile=config/logback.xml -cp .:lib/common.jar:lib/homeos-core.jar:lib/jwave.jar:lib/nrjavaserial.jar:lib/testgui.jar:lib/libsim.jar com/smahoo/kernel/HomeOs config.xml >> logs/homeos.log
