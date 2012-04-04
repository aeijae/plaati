package controllers

import org.codehaus.jackson.map.ObjectMapper
import play.api.mvc.{Action, Controller}
import models.{Song, Music, Album}
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object MusicController extends Controller {
  val mapper = new ObjectMapper()

  def albums = Action {
    Ok(mapper.writeValueAsString(Music.albums))
  }

  def albumById(id: String) = Action {
    Music.albumById(id) match {
      case Some(album) => Ok(mapper.writeValueAsString(album))
      case None => NotFound
    }
  }

  def songs = Action {
    Ok(mapper.writeValueAsString(Music.songs.asJava))
  }

  def songById(id: Int) = Action {
    Music.songById(id) match {
      case Some(song) => Ok(mapper.writeValueAsString(song))
      case None => NotFound
    }
  }
}
