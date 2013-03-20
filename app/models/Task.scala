package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._
import anorm.~
import anorm.Id

/**
 * Alien Invaders Ltd.
 * User: Aleksandr Khamutov
 * Date: 12.03.13
 * Time: 15:29
 */
case class Task(id: Pk[Long], name: String, status: String, board: Long)

object Task {

  val task = {
    long("id") ~
    str("name") ~
    str("status") ~
    long("board") map {
      case id~name~status~board_id => Task(Id(id), name, status, board_id)
    }
  }

  def by_board(bid: Long): List[Task] = DB.withConnection { implicit c =>
    SQL("select * from task where board = {board_id}").on(
      'board_id -> bid
    ).as(task *)
  }

  def all(): List[Task] = DB.withConnection { implicit c =>
    SQL("select * from task").as(task *)
  }

  def show(id: Long): Task = DB.withConnection { implicit c =>
    SQL("select * from task where id = {id}").on(
      'id -> id
    ).as(task.single)
  }

  def create(task: Task): Task = {
    DB.withConnection { implicit c =>
      val id: Option[Long] = SQL("insert into task (name, status, board) values ({name},{status},{board_id})").on(
        'name -> task.name,
        'status -> task.status,
        'board_id -> task.board
      ).executeInsert()
      task.copy(id = Id(id.get))
    }
  }

  def update(id: Long, task: Task) = {
    DB.withConnection { implicit c =>
      SQL(
        """
           update task
              set name = {name},
                  status = {status},
                  board = {board_id}
            where id = {id}
        """).on(
        'id -> id,
        'name -> task.name,
        'status -> task.status,
        'board_id -> task.board
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from task where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }

  implicit val taskFormat = (
      (__ \ "id").formatNullable[Long] and
      (__ \ "name").format[String] and
      (__ \ "status").formatNullable[String] and
      (__ \ "board_id").formatNullable[Long]
    )((id, name, status, board_id) => Task(id.map(Id(_)).getOrElse(NotAssigned), name, status.getOrElse("pending"), board_id.getOrElse(-1)),
    (t: Task) => (t.id.toOption, t.name, Some(t.status), Some(t.board)))
}