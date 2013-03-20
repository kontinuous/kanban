package controllers.login

import play.api.mvc._

/**
 * User: andrew
 * Date: 14.03.13
 * Time: 14:38
 */

/**
 * Provide security features
 */
trait Secured {

  private def username(request: RequestHeader) = request.session.get("name")

  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Login.login)

  def SecuredAction(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
  def SecuredAction[A](bodyParser: BodyParser[A])( f: => String => Request[A] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(bodyParser){ request =>
      f(user)(request)
    }
  }

//  def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
//    if(Project.isMember(project, user)) {
//      f(user)(request)
//    } else {
//      Results.Forbidden
//    }
//  }
//
//  def IsOwnerOf(task: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
//    if(Task.isOwner(task, user)) {
//      f(user)(request)
//    } else {
//      Results.Forbidden
//    }
//  }

}

