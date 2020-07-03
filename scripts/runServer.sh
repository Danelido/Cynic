#!/bin/sh
if apid=$(pgrep -f target/CynicServer-1.0.0.jar)
then
    echo "Server is running with pid: $apid, Killing process: $apid"
    kill -9 $apid
fi
echo "Cleaning and packaging Jar"
mvn clean package

echo "Running..."
java -jar CynicServer/target/CynicServer-1.0.0.jar &



