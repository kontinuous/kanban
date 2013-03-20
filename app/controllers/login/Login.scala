package controllers.login

import play.api._
import play.api.mvc._
import play.api.data._
import data.Forms._

import models._
import views._
import anorm.Id
import scala.Some

/**
 * User: andrew
 * Date: 14.03.13
 * Time: 13:37
 */
object Login extends Controller {

  val loginForm = Form(
    tuple(
      "login" -> text,
      "password" -> text
    ) transform(form => User.authenticate(form._1, form._2), (user: Option[User]) => (user map {
      u => Tuple2(u.name.get, "")
    } orElse {
      Some(Tuple2("", ""))
    }).get)
      verifying("Invalid email or password", result => result match {
      case user => user.isDefined
    })
  )

  val signupForm = Form(
    tuple(
      "name" -> text,
      "email" -> text,
      "password" -> text
    ) verifying("Any error has occurred", result => result match {
      case (name, email, password) => {
        User.create(User(name, Some(email), password))
        true
      }
    })
  )

  def login = Action {
    implicit request =>
      Ok(html.login.login(loginForm))
  }

  def logout = Action {
    Redirect(routes.Login.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  def authenticate = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.login.login(formWithErrors)),
        user => Redirect(controllers.routes.Application.index).withSession("name" -> user.get.name.get)
      )
  }

  def signup = Action {
    implicit request =>
      Ok(html.login.signup(signupForm))
  }

  def create = Action {
    implicit request =>
      signupForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.login.signup(formWithErrors)),
        user => Redirect(routes.Login.login())
      )
  }

}
