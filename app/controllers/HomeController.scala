package controllers

import javax.inject._
import models.Authenticator
import play.api.db.Database
import play.api.mvc._

/* This controller creates an `Action` to handle HTTP requests to the application's home page. */

@Singleton
class HomeController @Inject()(db: Database, cc: ControllerComponents) extends AbstractController(cc) {

  /* a basic controller to serve the index page and redirect to inbox if logged in */
  def index() = Action { implicit request: Request[AnyContent] =>

    val verified = Authenticator.verifyUserIdSession(db, request.session)
    if (verified) {
      Redirect("/inbox")
    } else {
      Ok(views.html.index())
    }
  }
}
