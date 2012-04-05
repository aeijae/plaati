import models.{User, Music, Album, Song}
import play.api._
import scala.io.Source._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object Global extends GlobalSettings {
  val AlbumNamePattern = """^([A-Z\s].*);(\d{4})""".r
  val SongNamePattern = """^(\d+);(\d+);"(.*)";(\d+):(\d+)""".r

  override def onStart(app: Application) {
    println("Loading data now!")

    Music.dropAlbums

    var currentAlbum: Option[(String, Short)] = None
    var songs = List[Song]()

    fromFile("conf/data.txt", "utf-8").getLines.zipWithIndex foreach  (e => e._1 match {

      case AlbumNamePattern(albumName, albumYear) => {
        println("Creating album " + albumName)
        currentAlbum match {
          case Some((n, y)) => {
            Music.saveAlbum(new Album(n, "Beatles", y, songs.asJava))
          }
          case None =>
        }
        songs = List()
        currentAlbum = Some((albumName, albumYear.toShort))
      }

      case SongNamePattern(songSide, songIndex, songName, lengthMin, lengthSec) => {
        songs = songs ::: List(new Song(e._2, songName, lengthMin.toShort * 60 + lengthSec.toShort, songIndex.toShort, songSide.toShort))
      }

      case str => println("No match for line " + str)
    })

    currentAlbum.map(a => a).foreach(a => Music.saveAlbum(new Album(a._1, "Beatles", a._2, songs.asJava)))

    User.save(new User("admin", "adminpwd", true))
  }
}
