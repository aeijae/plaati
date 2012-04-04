package models

import javax.persistence.Id
import reflect.BeanProperty
import org.codehaus.jackson.annotate.JsonProperty
import play.modules.mongodb.jackson.MongoDB
import net.vz.mongodb.jackson.ObjectId

class Song(@ObjectId @Id id: Int,
           @BeanProperty @JsonProperty("name") val name: String,
           @BeanProperty @JsonProperty("album") val album: String,
           @BeanProperty @JsonProperty("lengthInSecs") val length: Int,
           @BeanProperty @JsonProperty("index") val index: Short,
           @BeanProperty @JsonProperty("side") val side: Short = 0,
           @BeanProperty @JsonProperty("notes") val notes: String = "",
           @BeanProperty @JsonProperty("url") val url: String = "") {}

class Album(@ObjectId @Id id: Int,
            @BeanProperty @JsonProperty("name") val name: String,
            @BeanProperty @JsonProperty("artist") val artist: String,
            @BeanProperty @JsonProperty("year") val year: Short,
            @BeanProperty @JsonProperty("notes") val notes: String = "") {
}

object Album {
  import play.api.Play.current

  private lazy val db = MongoDB.collection("albums", classOf[Album], classOf[String])

  def save(album: Album) { db.save(album) }

  def getAlbums() = db.find().toArray

  def dropAlbums() = db.drop()
  }

object Song {
  import play.api.Play.current

  private lazy val db = MongoDB.collection("songs", classOf[Song], classOf[String])

  def save(song: Song) { db.save(song) }

  def getSongs() = db.find().toArray

  def dropSongs() = db.drop()

  def songsForAlbum()
}



