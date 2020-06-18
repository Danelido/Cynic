echo "Running Cynic Server..."
java -jar CynicServer/target/CynicServer-1.0-SNAPSHOT.jar &
echo "Running Cynic Server Test"
java -jar CynicServerTest/target/CynicServerTest-1.0-SNAPSHOT.jar "139.162.149.158" "13500"

if apid=$(pgrep -f target/CynicServer-1.0-SNAPSHOT.jar)
then
    echo "Killing server, pid was $apid"
    kill -9 $apid
fi