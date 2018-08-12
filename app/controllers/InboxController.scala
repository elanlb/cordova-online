package controllers

import javax.inject._
import models.Authenticator
import play.api.db._
import play.api.mvc._

/* This controller creates an `Action` to handle HTTP requests to the application's home page. */

@Singleton
class InboxController @Inject()(db: Database, cc: ControllerComponents) extends AbstractController(cc) {

  /* a controller to serve the inbox page */
  def inbox() = Action { implicit request: Request[AnyContent] =>

    val verified = Authenticator.verifyUserIdSession(db, request.session) // verify that the user id valid

    if (verified) {
      Ok("Inbox Page") // success
    } else {
      Redirect("/login")
    }
  }
}
