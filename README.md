# GPhotos Tagger

This is a webapp built with Play Framework and Scala used to automatically tag photos from Google Photos based on
certain categories. It is currently very in progress and might be finished by the end of the summer.

## How to use
This app is hosted on Heroku so that you don't have to set it up yourself. Just sign in with your Google account to get
started.

## How it works
Stuff happens.

### Running locally
This app can be run locally with the Heroku CLI. The Procfile must be edited for it to run correctly without a verified
SSL certificate. You also have to configure an OAuth2 application with Google.

- In `Procfile`, change `-Dhttp.port` to `-Dhttps.port` to enable HTTPS when running locally.
- Set an environment variable with your OAuth2 client ID: `export OAUTH2_CLIENT_ID="your client id"`.
- `sbt compile stage`
- `heroku local web`