package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import anorm.~
import anorm.Id
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * User: andrew
 * Date: 12.03.13
 * Time: 14:39
 */
case class User (name: Pk[String], password: String, salt: String, email: Option[String], isAdmin: Boolean = false)

object User {

//  def apply(name: Pk[String], password: String, salt: String, email: Option[String], isAdmin: Boolean = false) : User = {
//    User(name, password, salt, email, isAdmin)
//  }

  def apply(name: Pk[String], email: Option[String], password: String) : User = {
    val user = User(name, null, null, email)
    setPassword(user, password)
  }

  val user = {
    str("name") ~
      str("password") ~
      str("salt") ~
      get[Option[String]]("email") ~
      get[Boolean]("isAdmin") map {
      case name~password~salt~email~isAdmin =>
        User(Id(name), password, salt, email, isAdmin)
    }
  }

  def all(): List[User] = DB.withConnection { implicit c =>
    SQL("select * from user").as(user *)
  }

  def find(name: String): Option[User] = DB.withConnection { implicit c =>
    SQL("select * from user where name = {name}").on(
      'name -> name
    ).as(user.singleOpt)
  }

  def findByEmail(email: String): Option[User] = DB.withConnection { implicit connection =>
    SQL("select * from user where email = {email}").on(
      'email -> email
    ).as(user.singleOpt)
  }

  def create(user: User): User = {
    DB.withConnection { implicit c =>
      val id: Option[Long] = SQL("insert into user (name, password, salt, email, isAdmin) values ({name},{password},{salt},{email},{isAdmin})").on(
        'name -> user.name,
        'password -> user.password,
        'salt -> user.salt,
        'email -> user.email,
        'isAdmin -> user.isAdmin
      ).executeInsert()
      user
    }
  }

  def update(user: User) : User = {
    DB.withConnection { implicit c =>
      SQL("update user set password = {password}, salt = {salt}, isAdmin = {isAdmin}, email = {email} where name = {name}").on(
        'name -> user.name,
        'password -> user.password,
        'salt -> user.salt,
        'email -> user.email,
        'isAdmin -> user.isAdmin
      ).executeUpdate()
    }
    user
  }

  def delete(name: String) {
    DB.withConnection { implicit c =>
      SQL("delete from user where name = {name}").on(
        'name -> name
      ).executeUpdate()
    }
  }

  def setPassword(user: User, password: String) : User = {
    val key = BCrypt.gensalt()
    val hash = BCrypt.hashpw(password, key)
    user.copy(password = hash, salt = key)
  }

  def authenticate(email: String, password: String): Option[User] = {
    findByEmail(email) map {user =>
      val hash = BCrypt.hashpw(password, user.salt)
      if(hash == user.password) user else null
    }
  }

//  implicit val userFormat = (
//    (__ \ "name").format[String] and
//      (__ \ "email").formatNullable[String] and
//    )((id, name) => User(id.map(Id(_)).getOrElse(NotAssigned), name),
//    (b: User) => (b.id.toOption, b.name))
}