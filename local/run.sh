#!/bin/bash

sbt compile stage

export APPLICATION_SECRET="ThisIsASecret"
client_id=$( cat local/secret/client_id.txt )
export OAUTH2_CLIENT_ID="$client_id"
echo $OAUTH2_CLIENT_ID
export DATABASE_URL="jdbc:postgresql://localhost:5432/cordovalocal"

rm /Users/elan/Documents/Development/cordova-online/target/universal/stage/RUNNING_PID
heroku local -f local/Procfile.local web