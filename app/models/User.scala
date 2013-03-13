package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import org.mindrot.jbcrypt.BCrypt
import securesocial.core._
import anorm.~
import anorm.Id
import securesocial.core.OAuth2Info
import securesocial.core.OAuth1Info
import securesocial.core.PasswordInfo

/**
 * User: andrew
 * Date: 12.03.13
 * Time: 14:39
 */
case class User (uid: Pk[Long], name: String, email: Option[String], password: String, salt: Option[String])  extends Identity{

  def id: UserId = UserId(uid.toString, null)
  def firstName: String = name
  def lastName: String = null
  def fullName: String = null
  def avatarUrl: Option[String] = None
  def authMethod: AuthenticationMethod = AuthenticationMethod.UserPassword
  def oAuth1Info: Option[OAuth1Info] = None
  def oAuth2Info: Option[OAuth2Info] = None
  def passwordInfo: Option[PasswordInfo] = Some(PasswordInfo("bcrypt", password, salt))
}

object User {
  val user = {
    long("id") ~
      str("name") ~
      str("email") ~
      str("password") ~
      get[Option[String]]("salt") map {
      case id~name~email~password~salt =>
        User(Id(id), name, Some(email), password, salt)
    }
  }

  def all(): List[User] = DB.withConnection { implicit c =>
    SQL("select * from user").as(user *)
  }

  def find(id: Long): User = DB.withConnection { implicit c =>
    SQL("select * from user where id = {id}").on(
      'id -> id
    ).as(user.single)
  }

  def findByEmail(email: String): Option[User] = DB.withConnection { implicit connection =>
    SQL("select * from user where email = {email}").on(
      'email -> email
    ).as(user.singleOpt)
  }

  def create(user: User): User = {
    DB.withConnection { implicit c =>
      val id: Option[Long] = SQL("insert into user (name, email, password, salt) values ({name},{email},{password},{salt})").on(
        'name -> user.name,
        'email -> user.email,
        'password -> user.password,
        'salt -> user.salt
      ).executeInsert()
      user.copy(uid = Id(id.get))
    }
  }

  def update(id: Long, user: User) = {
    DB.withConnection { implicit c =>
      SQL("update user set name = {name}, email = {name}, password = {password}, salt = {salt} where id = {id}").on(
        'id -> id,
        'name -> user.name,
        'email -> user.email,
        'password -> user.password,
        'salt -> user.salt
      ).executeUpdate()
    }
  }

  def delete(id: Long) {
    DB.withConnection { implicit c =>
      SQL("delete from user where id = {id}").on(
        'id -> id
      ).executeUpdate()
    }
  }

//  def setPassword(user: User, password: String) : User = {
//    val key = BCrypt.gensalt()
//    val hash = BCrypt.hashpw(password, key)
//    user.copy(password = hash, salt = key)
//  }
//
//  def authenticate(email: String, password: String): Option[User] = {
//    val found: Option[User] = DB.withConnection { implicit connection =>
//    SQL("select * from user where email = {email}").on(
//    'email -> email
//    ).as(user.singleOpt)
//    }
//    found.map { u =>
//      val hash = BCrypt.hashpw(password, u.salt)
//      if (hash == u.password) u else null
//    }
//  }

//  implicit val userFormat = (
//    (__ \ "id").formatNullable[Long] and
//      (__ \ "name").format[String]
//    )((id, name) => User(id.map(Id(_)).getOrElse(NotAssigned), name),
//    (b: User) => (b.id.toOption, b.name))
}