#!/bin/bash

sudo launchctl load -F /Library/LaunchDaemons/com.oracle.oss.mysql.mysqld.plist

while ! nc -z localhost 3306 </dev/null; do sleep 1; done

echo "Please enter MySQL password for user: $1"
mysql --user=root --password < ./finalProjectBackEndScript.sql

sbt assembly

java -cp "./target/scala-2.12/hutchinsrfinalproject-assembly-0.1.jar" clients.NHLJsonClient $1 $2
echo "Database populated"
java -cp "./target/scala-2.12/hutchinsrfinalproject-assembly-0.1.jar" APIServer $1 $2 &

echo "Waiting on API server"
sleep 5
echo "API Server online"

open -a "Google Chrome" ./website/index.html 
