package controllers

import login.Secured
import play.api.libs.json.Json
import models.{Board, Task}
import play.api.mvc.{Controller, Action}
/**
 * Alien Invaders Ltd.
 * User: Aleksandr Khamutov
 * Date: 12.03.13
 * Time: 15:41
 */
object Tasks extends Controller with Secured {
  def index(bid: Long) = SecuredAction { name => request =>
    Ok(Json.toJson(Task.by_board(bid)))
  }

  def show(bid: Long, id: Long) = SecuredAction { name => request =>
    Ok(Json.toJson(Task.show(id)))
  }

  def update(bid: Long, id: Long) = SecuredAction(parse.json) { name => request =>
    request.body.validate[Task].fold(
      valid = { task =>
        Task.update(id, task)
        Ok(Json.toJson(Task.show(id)))
      },
      invalid = ( e => BadRequest(e.toString()) )
    )
  }

  def create(bid: Long) = SecuredAction(parse.json) { name => request =>
    request.body.validate[Task].fold(
      valid = { task =>
        val board = Board.show(bid)
        val savedTask = Task.create(task.copy(board = board.id.get))
        Ok(Json.toJson(savedTask))
      },
      invalid = ( e => BadRequest(e.toString()) )
    )
  }
}
