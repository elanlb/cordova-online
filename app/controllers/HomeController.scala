package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/* This controller creates an `Action` to handle HTTP requests to the application's home page. */

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /* There are two controllers here, one to open the main page and one for the about page.
   * More complicated pages like the sign in page have their own controllers. */

  def index () = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}
