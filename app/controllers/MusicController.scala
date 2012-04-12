package controllers

import play.api.mvc.{Action, Controller}
import models.{Song, Music, Album}
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object MusicController extends Controller with JsonWriting {

  def albums = Action {
    JsonOk(Music.albums)
  }

  def albumById(id: String) = Action {
    Music.albumById(id) match {
      case Some(album) => JsonOk(album)
      case None => NotFound
    }
  }

  def songs = Action {
    JsonOk(Music.songs.asJava)
  }

  def songById(id: Int) = Action {
    Music.songById(id) match {
      case Some(song) => JsonOk(song)
      case None => NotFound
    }
  }
}
