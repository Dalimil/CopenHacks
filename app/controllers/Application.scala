package controllers

import java.io.File

import com.google.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc._

import scala.util.Random

class Application @Inject() () extends Controller {
  var games:Map[String, Any] = Map()

  def index = Action { implicit request => // Make implicit to be able to use it later
    Logger.debug {
      new File(".").getCanonicalPath()
    }
    Ok(views.html.index("API")) // Send an html template
  }

  def getGames = TODO

  def getGame(id: Long) = TODO

  def addGame = Action(parse.multipartFormData) { implicit request =>
    val newGameData = newGameForm.bindFromRequest.get.data // request is implicit
    Logger.debug(newGameData)
    val photo = request.body.file("photo").get
    val filename = getFileName() + "." + getExtension(photo.filename)
    photo.ref.moveTo(new java.io.File(s"./public/storage/$filename"))
    games = games + (filename -> newGameData)

    val jsonVal: JsValue = Json.parse(newGameData)
    val minifiedString: String = Json.stringify(jsonVal)
    val readableString: String = Json.prettyPrint(jsonVal)
    Ok("File: "+ filename +" --- "+readableString)
  }

  def getExtension(name: String): String = {
    val dot = name.lastIndexOf(".")
    if(dot > 0) {
      name.substring(dot + 1)
    } else {
      ""
    }
  }

  def getFileName(): String = {
    var name = Random.nextInt(1000000).toString
    while(games.contains(name)) {
      name = Random.nextInt(1000000).toString
    }
    name
  }

  case class FormGameData(data: String)
  val newGameForm: Form[FormGameData] = Form(
    mapping("data" -> nonEmptyText)(FormGameData.apply)(FormGameData.unapply) // transform to our case class
  )

}

