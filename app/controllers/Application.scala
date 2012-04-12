package controllers

import views._
import play.api.data.Form

import play.api.mvc._
import play.api.data.Forms._

import models._
import org.codehaus.jackson.map.ObjectMapper


object Application extends Controller {
  
  def index = Action {
    Ok(html.index("Your new application is ready."))
  }

  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }

  def register = Action { implicit request =>
    Ok(html.register(registerUserForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.RatingController.rate).withSession("username" -> user._1)
    )
  }

  def createUser = Action { implicit request =>
    registerUserForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.register(formWithErrors)),
      newUser => {
        User.save(new User(newUser._1, newUser._2)) match {
          case Left(_) =>
            Redirect(routes.Application.login)

          case Right(_) => {
            BadRequest(html.register(registerUserForm))
          }
        }
      }
    )
  }

  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  val loginForm = Form (
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying ("Invalid username or password", result => result match {
      case (username, password) => User.authenticate(username, password).isDefined
    })
  )

  val registerUserForm = Form (
    tuple(
      "username" -> text,
      "password" -> text,
      "repeatPassword" -> text
    ) verifying ("Passwords don't match", result => result match {
      case (username, password, repeatPassword) => password == repeatPassword
    })
  )
}

trait Secured {

  private def username(request: RequestHeader) = request.session.get("username")

  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  def IsAdmin(f: => String => Request[AnyContent] => Result) = IsAuthenticated { username => request =>
    if(User.isAdmin(username)) {
      f(username)(request)
    } else {
      Results.Forbidden
    }
  }

}

trait JsonWriting extends Results {

  val mapper = new ObjectMapper()

  def JsonOk(value: Any) = Ok(mapper.writeValueAsString(value))
}

trait Measure {
  def measure(f: => Any): Any = {
    val time = System.currentTimeMillis()
    val ret = f
    println(System.currentTimeMillis() - time)
    ret
  }
}
