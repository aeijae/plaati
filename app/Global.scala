
import models.{Album, Song}
import play.api._
import scala.io.Source._
import java.util.ArrayList

object Global extends GlobalSettings {
  val AlbumNamePattern = """^([A-Z\s].*);(\d{4})""".r
  val SongNamePattern = """^(\d+);(\d+);"(.*)";(\d+):(\d+)""".r

  override def onStart(app: Application) {
    println("Loading data now")

    Album.dropAlbums

    var currentAlbum: Option[(String, Short)] = None
    var songs = new ArrayList[Song]

    fromFile("conf/data.txt", "utf-8").getLines.foreach(_ match {
      case AlbumNamePattern(albumName, albumYear) => {
        currentAlbum match {
          case Some((n, y)) => Album.save(new Album(n, "Beatles", y, songs))
          case None =>
        }
        songs = new ArrayList[Song]
        currentAlbum = Some((albumName, albumYear.toShort))
      }
      case SongNamePattern(songSide, songIndex, songName, lengthMin, lengthSec) => {
        songs.add(new Song(songName, lengthMin.toShort * 60 + lengthSec.toShort, songIndex.toShort, songSide.toShort))
      }
      case str => println("No match for " + str)
    })
  }
}
