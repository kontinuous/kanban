package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsSuccess, JsError, Json}
import models.Board
import models.Board.boardFormat

/**
 * Alien Invaders Ltd.
 * User: Aleksandr Khamutov
 * Date: 12.03.13
 * Time: 11:58
 */
object Boards extends Controller {
  def index = Action {
    Ok(Json.toJson(Board.all()))
  }

  def show(bid: Long) = Action {
    Ok(Json.toJson("1111"))
  }

  def update(bid: Long) = Action {
    Ok(Json.toJson("1111"))
  }

  def create = Action(parse.json) { request =>
    request.body.validate[Board].fold(
        valid = { board =>
          val savedBoard = Board.create(board)
          Ok(Json.toJson(savedBoard))
        },
        invalid = ( e => BadRequest(e.toString) )
      )
  }
}
