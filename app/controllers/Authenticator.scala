package controllers

import scala.collection.JavaConverters._
import javax.inject._
import play.api.db._
import play.api.mvc._
import play.api.Logger
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory


/* This controller loads the login page when it is requested and handles other sign in tasks */

@Singleton
class Authenticator @Inject()(db: Database, cc: ControllerComponents) extends AbstractController(cc) {

  // load the login page when it is requested through 'routes'
  def loginPage() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login())
  }

  // logout the user and send them to the logout page
  def logoutPage() = Action { implicit request: Request[AnyContent] =>
    val remoteIp = request.remoteAddress // get the remote ip

    db.withConnection{conn =>
      conn.createStatement().executeUpdate(
        s"UPDATE users SET ip = null WHERE ip = '$remoteIp'" // remove their ip session
      )
    }

    Ok(views.html.logout()) // user has been successfully logged out
  }

  // this gets called by google's OAuth2
  def tokenSignIn() = Action { implicit request: Request[AnyContent] =>
    val idToken = getIdToken(request) // verify the integrity of the token
    val userInfo = getUserInfo(idToken) // get the userInfo with the token
    loginToDatabase(userInfo) // check if the user has logged in already

    // set the user's ip in the database so that it carries over to other pages
    val userId = userInfo("userId")
    val remoteIp = request.remoteAddress // get the ip coming from the request

    db.withConnection{conn =>
      conn.createStatement.executeUpdate(
        s"UPDATE users SET ip = '$remoteIp' WHERE user_id = '$userId';"
      )
    }
    Redirect("/") // send them to the home page
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

    // check if the email is verified
    val emailVerified = payload.getEmailVerified
    if (!emailVerified) Logger.info("Email not verified")

    // Get profile information from payload
    val email = payload.getEmail
    val name = payload.get("name").toString
    val pictureUrl = payload.get("picture").toString

    /* Extra values for I don't know what
    val locale = payload.get("locale").toString
    val familyName = payload.get("family_name").toString
    val givenName = payload.get("given_name").toString
    */

    // put all of this information into a map to be returned
    val userInfo = Map(
      "userId" -> userId,
      "email" -> email,
      "name" -> name,
      "pictureUrl" -> pictureUrl
    )

    userInfo
  }

  def loginToDatabase(userInfo: Map[String, String]): Unit = {
    // connect to the database and log in the user
    db.withConnection{conn =>
      // select the user with a matching user id
      val userId = userInfo("userId")
      val resultSet = conn.createStatement.executeQuery(s"SELECT * FROM users WHERE user_id = '$userId';")

      // check if anything was returned (person has already logged in)
      if (resultSet.isBeforeFirst) {
        Logger.info(userInfo("name") + " has already logged in")

        // update the user's profile picture because it may have changed
        val pictureUrl = userInfo("pictureUrl")
        conn.createStatement.executeUpdate(s"UPDATE users SET picture_url = '$pictureUrl';")

        // the login was successful
      }
      else {
        Logger.info("Creating a new account for " + userInfo("name"))

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
