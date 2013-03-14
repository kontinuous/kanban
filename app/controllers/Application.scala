package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._

object Application extends Controller with login.Secured {
  
  def index = SecuredAction { username => request =>
    Ok(views.html.index(User.findByEmail(username).get))
  }
  
}