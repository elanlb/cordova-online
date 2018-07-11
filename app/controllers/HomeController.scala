package controllers

import javax.inject._
import play.api.db._
import play.api.mvc._
import play.api.Logger

/* This controller creates an `Action` to handle HTTP requests to the application's home page. */

@Singleton
class HomeController @Inject()(db: Database, cc: ControllerComponents) extends AbstractController(cc) {

  /* a basic controller to serve the index page */
  def index() = Action { implicit request: Request[AnyContent] =>
    // check if the user has logged in from this IP and if so redirect them
    val remoteIp = request.remoteAddress // get the remote IP

    db.withConnection{conn =>
      val user = conn.createStatement.executeQuery(
        s"SELECT * FROM users WHERE ip = '$remoteIp';"
      )

      // redirect to the mail page
      if (user.isBeforeFirst) {
        Redirect("/inbox")
      }
      else {
        Ok(views.html.index())
      }
    }
  }
}
