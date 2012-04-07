package controllers

import org.codehaus.jackson.map.ObjectMapper
import scala.collection.JavaConverters._
import models._
import play.api.mvc.{AnyContent, Request, Controller}

object RatingController extends Controller with Secured {
  val mapper = new ObjectMapper()

  def rate = IsAuthenticated { username => _ =>
    User.findByUsername(username).map { user =>
      Ok(views.html.rate(
        Music.albums.toList,
        user,
        Rating.ratingsByUser(username) map { r => (r.song, (r.rating, r.comment)) } toMap)
      )
    }.getOrElse(Redirect(routes.Application.login))
  }

  def rateSong(songId: Int, rating: Int) = IsAuthenticated { username => _ =>
    Rating.save(
      Rating.ratingsByUserAndSong(username, songId) match {
        case Some(oldRating : Rating) =>
          new Rating(oldRating.getId, songId, rating, username, oldRating.comment)

        case None =>
          new Rating(song = songId, user = username, rating = rating)
      }
    )
    Ok
  }

  def addComment(songId: Int) = IsAuthenticated { username => request => {
      val comment = request.body.asFormUrlEncoded.get("comment")(0)
      Rating.save(
        Rating.ratingsByUserAndSong(username, songId) match {
          case Some(oldRating : Rating) =>
            new Rating(oldRating.getId, songId, oldRating.rating, username, comment)

          case None =>
            new Rating(song = songId, user = username, comment = comment)
        }
      )
      Ok
    }
  }

  def ratings = IsAuthenticated { username => _ =>
    if (User.isAdmin(username)) {
      Ok(mapper.writeValueAsString(Rating.ratings.asJava))
    } else {
      Ok(mapper.writeValueAsString(Rating.ratingsByUser(username).asJava))
    }
  }

  def ratingsForSong(songId: Int) = IsAdmin { _ => _ =>
    Ok(mapper.writeValueAsString(Rating.ratingsBySong(songId).asJava))
  }
}