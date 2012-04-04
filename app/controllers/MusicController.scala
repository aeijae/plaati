package controllers

import org.codehaus.jackson.map.ObjectMapper
import play.api.mvc.{Action, Controller}
import scala.collection.JavaConversions._
import models.{Song, Album}

object MusicController extends Controller {
  val mapper = new ObjectMapper()

  def albums = Action {
    val albums = Album.getAlbums

    val mappedAlbums = albums.map { a: Album =>
      val jl = new java.util.ArrayList[Song] (a.getSongs().size)
      a.getSongs().foreach(jl.add(_))

      new Album(a.getName(), a.getArtist(), a.getYear(), jl)
    }

    Ok(mapper.writeValueAsString(albums))
  }

}
