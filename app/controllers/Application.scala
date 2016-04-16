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
  def addGame = TODO
  def showUser(name: String) = Action {
    val jsonVal: JsValue = Json.parse("""{"name": "Watership Down", "location": {"lat" : 51.23, "lng": -1.30}}""")
    val minifiedString: String = Json.stringify(jsonVal)
    val readableString: String = Json.prettyPrint(jsonVal)
    Ok("Hi: "+name+" --- "+readableString)
  }


}

