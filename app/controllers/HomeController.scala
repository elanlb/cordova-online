package controllers

import javax.inject._
import play.api.db.Database
import play.api.mvc.{ControllerComponents, AbstractController, Request, AnyContent}
import utilities.Authenticator

/* This controller creates an `Action` to handle HTTP requests to the application's home page. */

@Singleton
class HomeController @Inject()(db: Database, cc: ControllerComponents) extends AbstractController(cc) {
  val authenticator = new Authenticator(db) // this is used for accessing the database

  /* a basic controller to serve the index page and redirect to inbox if logged in */
  def index() = Action { implicit request: Request[AnyContent] =>

    val verified = authenticator.verifyUserIdSession(request.session)
    if (verified) {
      Redirect("/inbox")
    } else {
      Ok(views.html.index(request))
    }
  }
}
