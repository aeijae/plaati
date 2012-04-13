package controllers

import scala.collection.JavaConverters._
import models._
import play.api.mvc.Controller

object RatingController extends Controller with Secured with JsonWriting {

  def rate = IsAuthenticated { username => _ =>
    User.findByUsername(username).map { user => {
        val albums = Music.albums.toList
        val userRatings = Rating.ratingsByUser(username)

        Ok(views.html.rate(
          albums,
          user,
          userRatings map { r => (r.song, (r.rating, r.comment)) } toMap,
          new UserStats(albums, userRatings))
        )
      }
    }.getOrElse(Redirect(routes.Application.login))
  }

  def rateSong(songId: Int, rating: Int) = IsAuthenticated { username => _ =>
    Music.songs.find(_.id == songId) match {
      case Some(s: Song) => {
        Rating.save(storedOrNewRating(songId, Some(rating), username, None))
        Ok
      }
      case _ => BadRequest
    }
  }

  def addComment(songId: Int) = IsAuthenticated { username => request => {
      val comment = request.body.asFormUrlEncoded.get("comment")(0)
      Rating.save(
        storedOrNewRating(songId, None, username, Some(comment))
      )
      Ok
    }
  }

  def storedOrNewRating(songId: Int, rating: Option[Int], username: String, comment: Option[String]): Rating = {
    Rating.ratingsByUserAndSong(username, songId) match {
      case Some(r : Rating) =>
        new Rating(r.id, songId, rating.getOrElse(r.rating), username, comment.getOrElse(r.comment))
      case None =>
        new Rating(songId, rating.getOrElse(0), username, comment.getOrElse(""))
    }
  }

  def ratings = IsAuthenticated { username => _ =>
    if (User.isAdmin(username)) {
      JsonOk(Rating.ratings.asJava)
    } else {
      JsonOk(Rating.ratingsByUser(username).asJava)
    }
  }

  def ratingsForSong(songId: Int) = IsAdmin { _ => _ =>
    JsonOk(Rating.ratingsBySong(songId).asJava)
  }
}