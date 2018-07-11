package controllers

import javax.inject._
import play.api.db._
import play.api.mvc._

/* This controller creates an `Action` to handle HTTP requests to the application's home page. */

@Singleton
class InboxController @Inject()(db: Database, cc: ControllerComponents) extends AbstractController(cc) {

  /* a basic controller to serve the index page */
  def inbox() = Action { implicit request: Request[AnyContent] =>
    InternalServerError("Page in progress")
  }
}
