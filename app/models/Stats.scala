package models

import scala.collection.JavaConverters._
import reflect.BeanProperty
import org.codehaus.jackson.annotate.JsonProperty

class UserStats(albums: Seq[Album], ratings: Seq[Rating]) {
  def musicListened = ratings.foldLeft(0)((acc, r) =>  acc + songs.find(_.id == r.song).get.length)

  def ratedSongs = ratings.filter(_.rating > 0)

  def ratedAlbums = albums.filter(_.getSongs().asScala.forall(s => ratedSongs.map(_.song).contains(s.id)))

  def songs = albums.toList.map(_.getSongs().asScala.toList).flatten
}


case class SongStat(@BeanProperty @JsonProperty("song") song: Song,
                    @BeanProperty @JsonProperty("avgRating") avgRating: Float,
                    @BeanProperty @JsonProperty("ratings") ratings: java.util.List[Rating])

object SongStat {
  def buildStat(song: Song, songRatings: Seq[Rating]) =
    new SongStat(song, avg(songRatings), songRatings.asJava)

  def avg(songRatings: Seq[Rating]) =
    songRatings.foldLeft(0)((acc, s) => s.getRating() + acc).toFloat / songRatings.length
}
