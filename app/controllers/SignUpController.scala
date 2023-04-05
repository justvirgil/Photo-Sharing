package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._
import models.dao._
import models.domain._

@Singleton
class SignUpController @Inject()(userAccount: UserAccountDao, val controllerComponents: ControllerComponents, implicit val ec: ExecutionContext) extends BaseController with play.api.i18n.I18nSupport {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(signUpForm))
  }

  val signUpForm = Form(mapping(
    "user_id" -> ignored(0),
    "username" -> nonEmptyText,
    "password" -> nonEmptyText,
    "first_name" -> nonEmptyText,
    "last_name" -> nonEmptyText,
  )(UserAccount.apply)(UserAccount.unapply))


  def signUpUser = Action.async { implicit request: Request[AnyContent] =>
    signUpForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      {case user => 
        userAccount.createUser(user).map(_ => Redirect(routes.SignUpController.index()))
      }
    )
  }

  def findSpecificUser(id: Int) = Action.async { implicit request: Request[AnyContent] =>
      userAccount.findUser(id).map(_ => Ok("User Found"))
  }

  def signinUser() = Action.async { implicit request: Request[AnyContent] =>
    val formData = request.body.asFormUrlEncoded.getOrElse(Map.empty)
    val username = formData.get("username").flatMap(_.headOption).getOrElse("")
    val password = formData.get("password").flatMap(_.headOption).getOrElse("")
    userAccount.signin(username, password).map {
      case Some(user) => Ok(s"Welcome back, ${user.firstName} ${user.lastName}!")
      case None => BadRequest("Invalid username or password")
    }
  }
}
