package models

import play.api.Play.current
import javax.persistence.Id
import reflect.BeanProperty
import org.codehaus.jackson.annotate.JsonProperty
import play.modules.mongodb.jackson.MongoDB
import net.vz.mongodb.jackson.{DBQuery, ObjectId}

class User(@ObjectId @Id val id: String,
           @BeanProperty @JsonProperty("username") val username: String,
           @BeanProperty @JsonProperty("password") val password: String = "",
           @BeanProperty @JsonProperty("admin") val admin: Boolean = false) {

  @ObjectId @Id def getId = id


  def this(username: String, password: String) {
    this(null, username, password, false)
  }

  def this(username: String) {
    this(username, null)
  }

  def this(username: String, password: String, isAdmin: Boolean) {
    this(null, username, password, isAdmin)
  }
}

object User {
  import play.api.Play.current

  private lazy val db = MongoDB.collection("users", classOf[User], classOf[String])

  def findByUsername(username: String) = Option(db.findOne(DBQuery.is("username", username)))

  def isAdmin(username: String) = db.findOne(DBQuery.is("username", username)).admin

  def allUsers = db.find().toArray

  def authenticate(username: String, password: String) =
    Option(db.findOne(DBQuery.is("username", username).is("password", password)))

  def save(user: User) = findByUsername(user.username) match {
    case None => Left(db.save(user))
    case Some(_) => Right
  }

}
