package controllers

import javax.inject._
import play.api.db._
import play.api.mvc._
import models.Authenticator


/* This controller loads the login page when it is requested and handles other sign in tasks */

@Singleton
class LoginController @Inject()(db: Database, cc: ControllerComponents) extends AbstractController(cc) {

  // load the account page if they are logged in
  def accountPage() = Action { implicit request: Request[AnyContent] =>
    val verified = Authenticator.verifyUserIdSession(db, request.session)
    if (!verified) Redirect("/login")
    else InternalServerError("Account Page")
  }

  // load the login page when it is requested
  def loginPage() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login())
  }

  // logout the user and send them to the logout page
  def logoutPage() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.logout()).withSession(
      request.session - "userId"
    ) // user has been successfully logged out
  }

  // this gets called by google's OAuth2
  def tokenSignIn() = Action { implicit request: Request[AnyContent] =>
    val idToken = Authenticator.getIdToken(request) // verify the integrity of the token
    val userInfo = Authenticator.getUserInfo(idToken) // get the userInfo with the token
    Authenticator.loginToDatabase(db, userInfo) // check if the user has logged in already

    // get the user id from the user info
    val userId = userInfo("userId")

    // this will be received by xmlhttprequest and js will redirect them to the index page (/)
    Ok("/").withSession(
      "userId" -> userId // set the cookie to the user id
    ) // send them to the home page
  }
}
