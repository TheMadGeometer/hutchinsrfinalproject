#!/bin/bash

sudo launchctl load -F /Library/LaunchDaemons/com.oracle.oss.mysql.mysqld.plist

while ! nc -z localhost 3306 </dev/null; do sleep 1; done

sbt assembly

java -cp "./target/scala-2.12/hutchinsrfinalproject-assembly-0.1.jar" APIServer &

echo "Waiting on API server"
sleep 5
echo "API Server online"

open -a "Google Chrome" $1 
