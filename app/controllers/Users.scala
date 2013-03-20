package controllers

import login.Secured
import play.api.libs.json.Json
import models.{User, Board, Task}
import play.api.mvc.Controller

/**
 * Alien Invaders Ltd.
 * User: Aleksandr Khamutov
 * Date: 12.03.13
 * Time: 15:41
 */
object Users extends Controller with Secured {

  def index() = SecuredAction { name => request =>
    Ok(Json.toJson(User.all()))
  }

  def profile() = SecuredAction { name => request =>
    Ok(Json.toJson(User.find(name).get))
  }

  def show(uid: String) = SecuredAction { name => request =>
    Ok(Json.toJson(User.find(uid).get))
  }

  def shared() = SecuredAction { name => request =>
    Ok(Json.toJson(Board.findByUser(name)))
  }
}
