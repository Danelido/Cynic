#!/bin/sh
if apid=$(pgrep -f target/CynicServer-1.0.0.jar)
then
    echo "Running, pid is $apid"
    kill -9 $apid
fi
echo "Cleaning and packaging Jar"
mvn clean package

echo "Running..."
java -jar CynicServer/target/CynicServer-1.0.0.jar &



