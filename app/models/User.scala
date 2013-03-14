package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
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
case class User (name: Pk[String], password: String, email: Option[String], isAdmin: Boolean = false)  extends Identity{

  def id: UserId = UserId(name.get, null)
  def firstName: String = name.get
  def lastName: String = null
  def fullName: String = null
  def avatarUrl: Option[String] = None
  def authMethod: AuthenticationMethod = AuthenticationMethod.UserPassword
  def oAuth1Info: Option[OAuth1Info] = None
  def oAuth2Info: Option[OAuth2Info] = None
  def passwordInfo: Option[PasswordInfo] = Some(PasswordInfo("bcrypt", password, None))

}

object User {
  val user = {
    str("name") ~
      str("password") ~
      get[Option[String]]("email") ~
      get[Boolean]("isAdmin") map {
      case name~password~email~isAdmin =>
        User(Id(name), password, email, isAdmin)
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
      val id: Option[Long] = SQL("insert into user (name, password, email, isAdmin) values ({name},{password},{email},{isAdmin})").on(
        'name -> user.name,
        'password -> user.password,
        'email -> user.email,
        'isAdmin -> user.isAdmin
      ).executeInsert()
      user
    }
  }

  def update(user: User) : User = {
    DB.withConnection { implicit c =>
      SQL("update user set password = {password}, isAdmin = {isAdmin}, email = {email} where name = {name}").on(
        'name -> user.name,
        'password -> user.password,
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