package controllers

import com.google.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc._
import utils.{HttpClient, Scheduler}

class Application @Inject() () extends Controller {

  def index = Action { implicit request => // Make implicit to be able to use it later
    Logger.debug("Simple usage of the default logger")
    Ok(views.html.index("Your new application is ready")) // Send an html template
  }

  def getGames = TODO

  def getGame(id: Long) = TODO

  def addGame = Action(parse.multipartFormData) { implicit request =>
    val newGameData = newGameForm.bindFromRequest.get.data // request is implicit
    Logger.debug(newGameData)
    val photo = request.body.file("photo").get
    val filename = photo.filename
    photo.ref.moveTo(new java.io.File(s"/tmp/pictures/$filename"))

    val jsonVal: JsValue = Json.parse(newGameData)
    val minifiedString: String = Json.stringify(jsonVal)
    val readableString: String = Json.prettyPrint(jsonVal)
    Ok("File: "+ filename +" --- "+readableString)
  }

  case class FormGameData(data: String)
  val newGameForm: Form[FormGameData] = Form(
    mapping(
      "data" -> nonEmptyText,
    )(FormGameData.apply)(FormGameData.unapply) // to transform the data into an instance of our case class
  )

}

