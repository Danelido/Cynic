#!/bin/sh
if apid=$(pgrep -f target/MultiModeGameServerModule-1.0-SNAPSHOT.jar)
then
    echo "Running, pid is $apid"
    kill -9 $apid
fi
echo "Cleaning and packaging Jar"
mvn clean package

echo "Running..."
java -jar target/MultiModeGameServerModule-1.0-SNAPSHOT.jar &



