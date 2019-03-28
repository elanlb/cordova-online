package controllers

import javax.inject._
import play.api.db.Database
import play.api.mvc.{ControllerComponents, AbstractController, Request, AnyContent, RequestHeader}
import utilities.Authenticator


/* This controller loads the login page when it is requested and handles other sign in tasks */

@Singleton
class LoginController @Inject()(db: Database, cc: ControllerComponents) extends AbstractController(cc) {
  val authenticator = new Authenticator(db) // this is used for accessing the database

  // load the account page if they are logged in
  def accountPage() = Action { implicit request: Request[AnyContent] =>
    val verified = authenticator.verifyUserIdSession(request.session)
    if (!verified) Redirect("/login")
    else Ok("Account Page")
  }

  // load the login page when it is requested
  def loginPage() = Action { implicit request: Request[AnyContent] =>
    val verified = authenticator.verifyUserIdSession(request.session)
    if (verified) Redirect("/inbox") // send them to the inbox if they're logged in
    else Ok(views.html.login(request))
  }

  // logout the user and send them to the logout page
  def logoutPage() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.logout(request)).withSession(
      request.session - "userId"
    ) // user has been successfully logged out
  }

  // this gets called by google's OAuth2 POST
  def tokenSignIn() = Action { implicit request: Request[AnyContent] =>
    val requestIterator = request.body.asFormUrlEncoded.get("idtoken")
    val idToken = authenticator.getIdToken(requestIterator.mkString) // verify the integrity of the token
    val userInfo = authenticator.getUserInfo(idToken) // get the userInfo with the token
    authenticator.loginToDatabase(userInfo) // check if the user has logged in already

    // get the user id from the user info
    val userId = userInfo("userId")

    // this will be received by xmlhttprequest and js will redirect them to the index page (/)
    Ok("/").withSession(
      "userId" -> userId // set the cookie to the user id
    ) // send them to the home page
  }
}
