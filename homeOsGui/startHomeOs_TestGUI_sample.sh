#!/usr/bin/env bash

#!/usr/bin/env bash

path="."
for f in lib/*.jar
do
	path="$path:$f";
done

java -Dlogback.configurationFile=config/logback.xml -cp $path de/smahoo/homeos/testing/TestHomeOs config-sample.xml