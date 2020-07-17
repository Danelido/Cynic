echo "Running Cynic Server..."
java -jar CynicServer/target/CynicServer-1.0.0.jar &
echo "Running Cynic Server Test"
java -jar CynicServerTest/target/CynicServerTest-1.0.0.jar "127.0.0.1" "13500"

if apid=$(pgrep -f CynicServer/target/CynicServer-1.0.0.jar)
then
    echo "Killing server, pid was $apid"
    kill -9 $apid
fi