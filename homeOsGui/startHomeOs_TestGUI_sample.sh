#!/usr/bin/env bash
java -Dlogback.configurationFile=config/logback.xml -cp .:lib/homeos-core.jar:lib/nrjavaserial.jar:lib/testgui.jar:lib/libsim.jar de/smahoo/testing/homeos/TestHomeOs config-sample.xml