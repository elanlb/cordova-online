#!/bin/bash
sbt compile stage

export OAUTH2_CLIENT_ID="951199836711-7gsvltao1f7b6kg2jqtu6mkqvsl0m5r9.apps.googleusercontent.com"
heroku local web