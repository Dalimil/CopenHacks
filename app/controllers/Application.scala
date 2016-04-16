package controllers

import java.io.File

import com.google.inject.Inject
import models.{Db, Game}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc._

import scala.util.Random

class Application @Inject() () extends Controller {

  def index = Action { implicit request => // Make implicit to be able to use it later
    Logger.debug(new File(".").getCanonicalPath)
    Ok(views.html.index("API")) // Send an html template
  }

  def getGames = Action {
    val games = Db.query[Game].fetch
    val result: JsObject = Json.obj("games" -> games.toList.map {
      g => Json.obj("assets/"+g.name -> Json.parse(g.config))
    })
    Logger.debug(Json.prettyPrint(result))
    Ok(result)
  }

  def addGame = Action(parse.multipartFormData) { implicit request =>
    val newGameData = newGameForm.bindFromRequest.get.data // request is implicit
    Logger.debug(newGameData)
    val photo = request.body.file("photo").get
    val filename = getFileName(getExtension(photo.filename))
    Logger.debug("new dirs: " + new java.io.File("./public/storage/").mkdirs())
    photo.ref.moveTo(new java.io.File(s"/tmp/$filename"))
    val newGame = Game(filename, newGameData)
    Db.save(newGame)

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

  def getFileName(extension: String): String = {
    var name = Random.nextInt(1000000).toString + "." + extension
    while(Db.query[Game].whereEqual("name", name).exists()) {
      name = Random.nextInt(1000000).toString + "." + extension
    }
    name
  }

  case class FormGameData(data: String)
  val newGameForm: Form[FormGameData] = Form(
    mapping("data" -> nonEmptyText)(FormGameData.apply)(FormGameData.unapply) // transform to our case class
  )

}

