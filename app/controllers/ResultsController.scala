package controllers

import play.api.mvc.Controller
import scala.collection.JavaConverters._
import models.{SongStat, Music, Rating}
import views.html

object ResultsController extends Controller with Secured with JsonWriting with Measure {

  def results(position: Int) = IsAdmin { _ => _ => {
      Ok(html.result(songStats(position)))
    }
  }

  def allStats = IsAdmin { _ => _ =>
    JsonOk(songStats.asJava)
  }

  private def songStats =
    ratedSongs.map(song =>SongStat.buildStat(song, Rating.ratedRatingsBySong(song.id))).sortBy(_.avgRating)

  private def ratedSongs = Music.songs.filter(song => Rating.ratedRatingsBySong(song.id).length > 0)
}
