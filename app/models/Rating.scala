package models

import javax.persistence.Id
import reflect.BeanProperty
import org.codehaus.jackson.annotate.JsonProperty
import play.modules.mongodb.jackson.MongoDB
import net.vz.mongodb.jackson.{DBQuery, ObjectId}

class Rating(@ObjectId @Id val id: String,
             @BeanProperty @JsonProperty("songId") val song: Int,
             @BeanProperty @JsonProperty("rating") val rating: Int,
             @BeanProperty @JsonProperty("user") val user: String) {

  @ObjectId @Id def getId = id

  def this(song: Int, rating: Int, user: String) = this(null, song, rating, user)
}

object Rating {
  import play.api.Play.current

  private lazy val db = MongoDB.collection("ratings", classOf[Rating], classOf[String])

  def save(rating: Rating) { db.save(rating) }

  def findByUser(user: String) = db.find().is("user", user).toArray

  def findByUserAndSong(user: String, songId: Long) = Option(db.findOne(DBQuery.is("user", user).is("song", songId)))

  def findBySong(songId: Int) = db.find().is("song", songId).toArray
}
