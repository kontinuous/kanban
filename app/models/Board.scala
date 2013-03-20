package models

import anorm._
import play.api.db.DB
import anorm.SqlParser._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._
import anorm.~
import play.api.libs.json.JsObject
import play.api.libs.json.JsString

/**
 * Alien Invaders Ltd.
 * User: Aleksandr Khamutov
 * Date: 12.03.13
 * Time: 12:00
 */

case class Board(id: Pk[Long], name: String, owner: String)

object Board {
  val board = {
    long("id") ~
    str("name") ~
    str("owner") map {
      case id~name~owner => Board(Id(id), name, owner)
    }
  }

  def all(): List[Board] = DB.withConnection { implicit c =>
    SQL("select * from board").as(board *)
  }

  def show(id: Long): Board = DB.withConnection { implicit c =>
    SQL("select * from board where id = {id}").on(
      'id -> id
    ).as(board.single)
  }

  def create(board: Board): Board = {
    DB.withConnection { implicit c =>
      val id: Option[Long] = SQL("insert into board (name, owner) values ({name}, {owner})").on(
        'name -> board.name,
        'owner -> board.owner
      ).executeInsert()
      board.copy(id = Id(id.get))
    }
  }

  def update(id: Long, board: Board) = {
    DB.withConnection { implicit c =>
      SQL("update board set name = {name} where id = {id}").on(
        'id -> id,
        'name -> board.name,
        'owner -> board.owner
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from board where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }

  def findByUser(user: String) : List[Board] = DB.withConnection { implicit c =>
    SQL("select b.* from user_board ub inner join board b on ud.id = b.board_id where ub.user_name = {name}").on(
      'name -> user
    ).as(Board.board *)
  }

  def findByOwner(owner: String) : List[Board] = DB.withConnection { implicit c =>
    SQL("select b.* from board b where b.owner = {name}").on(
      'name -> owner
    ).as(Board.board *)
  }

  def shareToUser(board: Board, user: String) {
    DB.withConnection { implicit c =>
      SQL("insert into user_board (user_name, board_id) values ({name}, {bid})").on(
        'name -> user,
        'bid -> board.id
      ).executeInsert()
    }
  }

  def banUser(board: Board, user: String) {
    DB.withConnection { implicit c =>
      SQL("delete from user_board where user_name = {name} and board_id = {bid}").on(
        'name -> user,
        'bid -> board.id
      ).executeUpdate()
    }
  }

  implicit val boardFormat = (
      (__ \ "id").formatNullable[Long] and
      (__ \ "name").format[String] and
      (__ \ "owner").formatNullable[String]
    )((id, name, owner) => Board(id.map(Id(_)).getOrElse(NotAssigned), name, owner.getOrElse(null)),
      (b: Board) => (b.id.toOption, b.name, Some(b.owner)))
}
