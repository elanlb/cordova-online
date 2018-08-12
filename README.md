# Super Inbox

[![Build Status](https://travis-ci.org/elanlb/super-inbox.svg?branch=master)](https://travis-ci.org/elanlb/super-inbox)

Super Inbox is a webapp built with Play Framework and Scala to access mail from different email accounts in one place.

## How to use
This app is hosted on Heroku so that you don't have to set it up yourself. Just sign in with your Google account to get
started.

## How it works
Stuff happens.

## Running locally
This app can be run locally with the Heroku CLI. The Procfile must be edited for it to run correctly without a verified
SSL certificate. You also have to configure an OAuth2 application with Google.

- In `Procfile`, change `-Dhttp.port` to `-Dhttps.port` to enable HTTPS when running locally.
- Set these environment variables:
  - `export OAUTH2_CLIENT_ID="your client id"`
  - `export DATABASE_DRIVER="org.postgresql.Driver"`
  - `export DATABASE_URL="jdbc:postgresql://localhost:5432/name of database"`
- `sbt compile stage`
- `heroku local web`