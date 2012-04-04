package controllers

import play.api.mvc.{Action, Controller}
import org.codehaus.jackson.map.ObjectMapper
import models.Rating

object RatingController extends Controller {
  val mapper = new ObjectMapper()

  def rateSong(songId: Int, rating: Int, user: String) = Action {
    Rating.save(Rating.findByUserAndSong(user, songId) match {
        case Some(oldRating : Rating) => new Rating(oldRating.getId, songId, rating, user)
        case None => new Rating(songId, rating, user)
    })

    Ok
  }

  def ratingsForUser(user: String) = Action {
    Ok(mapper.writeValueAsString(Rating.findByUser(user)))
  }
}