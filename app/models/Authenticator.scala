package models

import scala.collection.JavaConverters._
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import play.api.Logger
import play.api.db._
import play.api.mvc._

object Authenticator {

  // use this function to check if the user is logged in (through cookies)
  def verifyUserIdSession(db: Database, session: Session): Boolean = {
    session.get("userId").map { userId =>

      db.withConnection { conn =>
        // get the name then check if it's empty or not
        val resultSet = conn.createStatement().executeQuery(s"SELECT * FROM users WHERE user_id = '$userId';")

        resultSet.next() // go to the first entry
        if (resultSet.getString("name").isEmpty) {
          return false // invalid userid
        } else {
          return true // valid user
        }
      } // close connection

    }.getOrElse {
      return false // user ID doesn't exist
    }
  }

  // called by tokenSignIn by Google
  def getIdToken(request: Request[AnyContent]): GoogleIdToken = {
    // get the oauth2 information from the environment variables
    val oauth2_client_id = System.getenv("OAUTH2_CLIENT_ID")

    // create a GoogleIdTokenVerifier to verify integrity
    val transport = new NetHttpTransport()
    val jsonFactory = new JacksonFactory()
    val verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
      // Specify the CLIENT_ID of the app that accesses the backend:
      .setAudience(List(oauth2_client_id).asJava)
      // Or, if multiple clients access the backend:
      //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
      .build()

    // (Receive idTokenString by HTTPS POST)
    val idTokenString = request.body.asFormUrlEncoded.get("idtoken")
    val idToken = verifier.verify(idTokenString.mkString)

    // return the idToken to be used in the future (it is assumed to be verified)
    if (Option(idToken).isEmpty) {
      Logger.info("OAuth2 verification failed")
    }

    idToken
  }

  // verify the integrity of the Google ID token and return user info
  def getUserInfo(idToken: GoogleIdToken): Map[String, String] = {
    val payload = idToken.getPayload

    val userId = payload.getSubject // get user identifier

    // Get profile information from payload
    val email = payload.getEmail
    val name = payload.get("name").toString
    val pictureUrl = payload.get("picture").toString

    /* Extra values for I don't know what
    val locale = payload.get("locale").toString
    val familyName = payload.get("family_name").toString
    val givenName = payload.get("given_name").toString
    */

    // check if the email is verified
    val emailVerified = payload.getEmailVerified
    if (!emailVerified) Logger.warn(s"Email not verified ($email)")

    // put all of this information into a map to be returned
    val userInfo = Map(
      "userId" -> userId,
      "email" -> email,
      "name" -> name,
      "pictureUrl" -> pictureUrl
    )

    userInfo
  }

  def loginToDatabase(db: Database, userInfo: Map[String, String]): Unit = {
    // connect to the database and log in the user
    db.withConnection { conn =>
      // select the user with a matching user id
      val userId = userInfo("userId")
      val resultSet = conn.createStatement.executeQuery(s"SELECT * FROM users WHERE user_id = '$userId';")

      // check if anything was returned (person has already logged in)
      if (resultSet.isBeforeFirst) {

        // update the user's profile picture because it may have changed
        val pictureUrl = userInfo("pictureUrl")
        conn.createStatement.executeUpdate(s"UPDATE users SET picture_url = '$pictureUrl';")

        // the login was successful

      } else {

        // add a new user to the table
        // get the user information values
        val email = userInfo("email")
        val name = userInfo("name")
        val pictureUrl = userInfo("pictureUrl")

        // execute the query
        conn.createStatement.executeUpdate(
          s"INSERT INTO users (user_id, email, name, picture_url) VALUES ('$userId', '$email', '$name', '$pictureUrl');"
        )
      }
    } // close the connection
  }
}
