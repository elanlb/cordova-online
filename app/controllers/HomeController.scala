package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/* This controller creates an `Action` to handle HTTP requests to the application's home page. */

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /* a basic controller to serve the index page */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}
