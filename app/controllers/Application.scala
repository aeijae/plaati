package controllers

import play.api.mvc._
import models.Music
import scala.collection.JavaConverters._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def measure = Action {
    Ok(views.html.measure(Music.albums.asScala.toList))
  }
  
}