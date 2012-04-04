package models

import javax.persistence.Id
import reflect.BeanProperty
import org.codehaus.jackson.annotate.JsonProperty
import play.modules.mongodb.jackson.MongoDB
import net.vz.mongodb.jackson.ObjectId
import scala.collection.JavaConversions._

class Song(@BeanProperty @JsonProperty("id") val id: Int,
           @BeanProperty @JsonProperty("name") val name: String,
           @BeanProperty @JsonProperty("lengthInSecs") val length: Int,
           @BeanProperty @JsonProperty("index") val index: Short,
           @BeanProperty @JsonProperty("side") val side: Short = 0,
           @BeanProperty @JsonProperty("notes") val notes: String = "",
           @BeanProperty @JsonProperty("url") val url: String = "") {}

class Album(@JsonProperty("id") @ObjectId @Id id: String,
            @BeanProperty @JsonProperty("name") val name: String,
            @BeanProperty @JsonProperty("artist") val artist: String,
            @BeanProperty @JsonProperty("year") val year: Short,
            @BeanProperty @JsonProperty("songs") val songs: java.util.List[Song] = null,
            @BeanProperty @JsonProperty("notes") val notes: String = "") {

  @ObjectId @Id def getId = id

  def this(name: String, artist: String, year: Short, songs: java.util.List[Song]) {
    this(null, name, artist, year, songs)
  }
}

object Music {
  import play.api.Play.current

  private lazy val db = MongoDB.collection("albums", classOf[Album], classOf[String])

  def saveAlbum(album: Album) { db.save(album) }

  def albums = db.find().toArray

  def dropAlbums = db.drop()

  def albumById(albumId: String) = Option(db.findOneById(albumId))

  def songs = albums.foldLeft(List[Song]()){ (b, a) => b ++ a.getSongs }

  def songById(songId: Int) = songs.find(_.id == songId)
}


