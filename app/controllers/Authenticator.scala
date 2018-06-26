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

  // this gets called by OAuth2
  def tokenSignIn() = Action { implicit request: Request[AnyContent] =>
    val idToken = getIdToken(request) // verify the integrity of the token
    val userInfo = getUserInfo(idToken) // get the userInfo with the token
    loginToDatabase(userInfo) // check if the user has logged in already

    // set a session variable (cookie) with the action for the other pages to use
    Ok(views.html.index()).withSession("userId" -> userInfo("userId"))
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
    if (Option(idToken).isDefined) {
      Logger.debug("OAuth2 verification success")
    }
    else {
      Logger.debug("OAuth2 verification failed")
    }

    idToken
  }

  // verify the integrity of the Google ID token and return user info
  def getUserInfo(idToken: GoogleIdToken): Map[String, String] = {
    val payload = idToken.getPayload

    // Print user identifier// Print user identifier
    val userId = payload.getSubject
    Logger.debug("User ID: " + userId)

    // check if the email is verified
    val emailVerified = payload.getEmailVerified
    if (!emailVerified) Logger.debug("Email not verified")

    // Get profile information from payload
    val email = payload.getEmail
    val name = payload.get("name").toString
    val pictureUrl = payload.get("picture").toString
    //val locale = payload.get("locale").toString
    //val familyName = payload.get("family_name").toString
    //val givenName = payload.get("given_name").toString

    // put all of this information into a map to be returned
    val userInfo = Map(
      "userId" -> userId,
      "email" -> email,
      "name" -> name,
      "pictureUrl" -> pictureUrl
    )

    Logger.debug(userInfo("name"))

    userInfo
  }

  def loginToDatabase(userInfo: Map[String, String]): Unit = {
    // connect to the database and log in the user
    db.withConnection{conn =>
      // check if the user is already logged in (will return true if anything is returned)
      val userId = userInfo("userId")
      val resultSet = conn.createStatement.execute(s"""SELECT * FROM users WHERE user_id is "$userId" """)

      if (resultSet) {
        Logger.debug(userInfo("name") + " has already logged in")

        // update the user's profile picture, just in case it was changed since they last logged in
        val pictureUrl = userInfo("pictureUrl")
        conn.createStatement.executeUpdate(s"""UPDATE users SET picture_url="$pictureUrl" """)

        // the login was successful
      }
      else {
        // add a new user to the table
        // get the user information values
        val email = userInfo("email")
        val name = userInfo("name")
        val pictureUrl = userInfo("pictureUrl")

        // execute the query
        conn.createStatement.executeUpdate(
          s"""INSERT INTO users (user_id, email, name, picture_url) VALUES ("$userId", "$email", "$name", "$pictureUrl") """
        )
      }
    }
  }
}
