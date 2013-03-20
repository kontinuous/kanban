package controllers

import play.api.mvc._

import models._

object Application extends Controller with login.Secured {
  
  def index = SecuredAction { username => request =>
    Ok(views.html.index(User.find(username).get))
  }
  
}