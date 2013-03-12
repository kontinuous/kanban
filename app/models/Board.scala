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

case class Board(id: Pk[Long], name: String)

object Board {
  val board = {
    long("id") ~
    str("name") map {
      case id~name => Board(Id(id), name)
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
      val id: Option[Long] = SQL("insert into board (name) values ({name})").on(
        'name -> board.name
      ).executeInsert()
      board.copy(id = Id(id.get))
    }
  }

  def update(id: Long, board: Board) = {
    DB.withConnection { implicit c =>
      SQL("update board set name = {name} where id = {id}").on(
        'id -> id,
        'name -> board.name
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

  implicit val boardFormat = (
      (__ \ "id").formatNullable[Long] and
      (__ \ "name").format[String]
    )((id, name) => Board(id.map(Id(_)).getOrElse(NotAssigned), name),
      (b: Board) => (b.id.toOption, b.name))
}
