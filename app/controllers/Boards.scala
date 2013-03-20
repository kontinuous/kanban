package controllers

import login.Secured
import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import models.Board

/**
 * Alien Invaders Ltd.
 * User: Aleksandr Khamutov
 * Date: 12.03.13
 * Time: 11:58
 */
object Boards extends Controller with Secured {
  def index = SecuredAction { name => request =>
    Ok(Json.toJson(Board.findByOwner(name)))
  }

  def show(bid: Long) = SecuredAction { name => request =>
    Ok(Json.toJson(Board.show(bid)))
  }

  def update(bid: Long) = SecuredAction(parse.json) { name => request =>
    request.body.validate[Board].fold(
      valid = { board =>
        Board.update(bid, board)
        Ok(Json.toJson(Board.show(bid)))
      },
      invalid = ( e => BadRequest(e.toString) )
    )
  }

  def create = SecuredAction(parse.json) { name => request =>
    request.body.validate[Board].fold(
        valid = { board =>
          val savedBoard = Board.create(board.copy(owner = name))
          Ok(Json.toJson(savedBoard))
        },
        invalid = ( e => BadRequest(e.toString) )
      )
  }
}
