package controllers

import scala.collection.JavaConverters._
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.Logger
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

/* This controller loads the login page when it is requested and handles other sign in tasks */

@Singleton
class Authenticator @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // load the login page when it is requested through 'routes'
  def loginPage () = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.login())
  }

  def tokenSignIn () = Action { implicit request: Request[AnyContent] =>
    // call the verifyToken fuction to verify the integrity of the token
    val tokenVerified = verifyToken(request)

    if (tokenVerified) {
      Logger.debug("success!")
    }
    else {
      Logger.debug("fail")
    }

    Ok(views.html.index())
  }

  // verify the integrity of the Google ID token
  def verifyToken (request: Request[AnyContent]) : Boolean = {
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

    if (idToken != null) {
      val payload = idToken.getPayload

      // Print user identifier// Print user identifier
      val userId = payload.getSubject
      Logger.debug("User ID: " + userId)

      true
    }
    else {
      false
    }
  }
}
