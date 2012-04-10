import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "plaati"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "net.vz.mongodb.jackson" % "play-mongo-jackson-mapper_2.9.1" % "1.0.0-rc.3",
      "net.vz.mongodb.jackson" % "mongo-jackson-mapper" % "1.4.1",
      "org.expressme" % "JOpenId" % "1.08",
      "org.jretrofit" % "jretrofit" % "1.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      resolvers += "Sonatype repository" at "https://oss.sonatype.org/content/repositories/releases/"
    )

}
