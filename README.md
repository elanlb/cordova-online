# Cordova Online

## Running locally
This app can be run locally with the Heroku CLI. The Procfile must be edited for it to run correctly without a verified
SSL certificate. You also have to configure an OAuth2 application with Google.

- In `Procfile`, change `-Dhttp.port` to `-Dhttps.port` to enable HTTPS when running locally.
- Set these environment variables:
  - `export APPLICATION_SECRET="some secret code"`
  - `export OAUTH2_CLIENT_ID="your client id"`
  - `export DATABASE_URL="jdbc:postgresql://localhost:5432/name of database"`
- `sbt compile stage`
- `heroku local web`