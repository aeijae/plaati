package models

import javax.persistence.Id
import reflect.BeanProperty
import org.codehaus.jackson.annotate.JsonProperty
import play.modules.mongodb.jackson.MongoDB
import net.vz.mongodb.jackson.{DBQuery, ObjectId}
import scala.collection.JavaConverters._

class Rating(@ObjectId @Id val id: String,
             @BeanProperty @JsonProperty("songId") val song: Int,
             @BeanProperty @JsonProperty("rating") val rating: Int = 0,
             @BeanProperty @JsonProperty("user") val user: String,
             @BeanProperty @JsonProperty("comment") val comment: String = "") {

  @ObjectId @Id def getId = id

  def this(songId: Int, rating: Int, user: String, comment: String) {
    this(null, songId, rating, user, comment)
  }
}

object Rating {
  import play.api.Play.current

  private lazy val db = MongoDB.collection("ratings", classOf[Rating], classOf[String])

  def save(rating: Rating) { db.save(rating) }

  def ratings = db.find().toArray.asScala

  def ratingsByUser(user: String) = db.find().is("user", user).toArray.asScala

  def ratingsByUserAndSong(user: String, songId: Long) =
    Option(db.findOne(DBQuery.is("user", user).is("song", songId)))

  def ratingsBySong(songId: Int) = db.find().is("song", songId).toArray.asScala

  def ratedRatingsBySong(songId: Int) = ratingsBySong(songId).filter(_.rating > 0)
}
