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
      g => Json.obj("url" -> Json.toJson("assets/"+g.name), "config" -> Json.parse(g.config))
    })
    Logger.debug(Json.prettyPrint(result))
    Ok(result)
  }

  def addGame = Action(parse.multipartFormData) { implicit request =>
    val newGameData = newGameForm.bindFromRequest.get.data // request is implicit
    Logger.debug(newGameData)
    val photo = request.body.file("photo").get
    val filename = getFileName(getExtension(photo.filename))
    val path = if(play.api.Play.isProd(play.api.Play.current)){
      "../public/storage/"
    } else "./public/storage/"
    Logger.debug("New dirs: " + new java.io.File(path).mkdirs())
    photo.ref.moveTo(new java.io.File(s"$path$filename"))
    val newGame = Game(filename, newGameData)
    Db.save(newGame)

    val jsonVal: JsValue = Json.parse(newGameData)
    val minifiedString: String = Json.stringify(jsonVal)
    val readableString: String = Json.prettyPrint(jsonVal)
    Ok("File: "+ filename +" --- "+readableString)
  }

  def delete(id: String) = Action {
    if(id.isEmpty){
      Db.query[Game].fetch.foreach(Db.delete[Game])
    } else {
      Db.query[Game].whereEqual("name", id).fetch.foreach(Db.delete[Game])
    }
    Ok("Deleted "+id)
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

