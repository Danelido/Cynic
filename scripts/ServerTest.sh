echo "Running Cynic Server..."
java -jar CynicServer/target/CynicServer-1.0.0.jar &
echo "Running Cynic Server Test"
java -jar CynicServerTest/target/CynicServerTest-1.0.0.jar "139.162.149.158" "13500"
