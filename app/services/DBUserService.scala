package services

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.providers.Token
import scala.Some
import org.mindrot.jbcrypt.BCrypt
import models.User
import anorm.Id


/**
 * User: andrew
 * Date: 13.03.13
 * Time: 12:06
 */


class DBUserService(application: Application) extends UserServicePlugin(application) {
  private var tokens = Map[String, Token]()

  def find(id: UserId): Option[Identity] = {
    User.findByEmail(id.id)
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    User.findByEmail(email)
  }

  def save(user: Identity): Identity = {
    User.find(user.id.id) match {
      case Some(u: User) => User.update(u)
      case None => User.create(
        User(Id(user.firstName),
          user.passwordInfo.get.password,
          user.email))
    }
  }

  def save(token: Token) {
    tokens += (token.uuid -> token)
  }

  def findToken(token: String): Option[Token] = {
    tokens.get(token)
  }

  def deleteToken(uuid: String) {
    tokens -= uuid
  }

  def deleteTokens() {
    tokens = Map()
  }

  def deleteExpiredTokens() {
    tokens = tokens.filter(!_._2.isExpired)
  }
}