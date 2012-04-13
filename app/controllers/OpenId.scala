package controllers

import java.text.SimpleDateFormat
import play.api.mvc.Action._
import play.api.mvc.{Controller, Action, AnyContent, Request}
import collection.mutable.MutableList
import javax.servlet.http.HttpServletRequest
import org.jretrofit.Retrofit
import models.User
import org.expressme.openid.{Base64, OpenIdManager, OpenIdException}

object OpenId extends Controller {

  val ATTR_MAC = "openid_mac"
  val ATTR_ALIAS = "openid_alias"

  val nonces = new MutableList[String]
  val manager = new OpenIdManager

  manager.setReturnTo("http://localhost/openId")
  manager.setRealm("http://localhost")

  def openId = Action {
    implicit request: Request[AnyContent] =>
      request.queryString.get("op") match {
        case Some(Seq("Google")) => {
          providerRedirect
        }
        case _ => {
          authenticate
        }
      }
  }

  def providerRedirect() = {
    val endpoint = manager.lookupEndpoint("Google")
    val association = manager.lookupAssociation(endpoint)
    val url = manager.getAuthenticationUrl(endpoint, association)

    Redirect(url).withSession(ATTR_MAC -> association.getMacKey, ATTR_ALIAS -> endpoint.getAlias)
  }

  def authenticate(implicit request: Request[AnyContent]) = {
    try {
      checkNonce(request.queryString("openid.response_nonce")(0))

      val authentication = manager.getAuthentication(
        Retrofit.partial(new RequestWrapper(request), classOf[HttpServletRequest]),
        Base64.decode(session.get(ATTR_MAC).get),
        session.get(ATTR_ALIAS).get)

      User.findByUsername(authentication.getIdentity) match {
        case None => User.save(new User(authentication.getEmail))
        case _ =>
      }

      Redirect(routes.RatingController.rate)
        .withSession("username" -> authentication.getEmail)

    } catch {
      case e: Exception => {
        e.printStackTrace()
        Forbidden
      }
    }
  }

  def checkNonce(nonce: String) {
    val ONE_HOUR = 3600000L
    val TWO_HOUR = ONE_HOUR * 2L

    if (nonce == null || nonce.length < 20)
      throw new OpenIdException("Verify failed.")

    var nonceTime = getNonceTime(nonce)

    if (math.abs(System.currentTimeMillis() - nonceTime) > ONE_HOUR)
      throw new OpenIdException("Bad nonce time.")
    if (isNonceExist(nonce))
      throw new OpenIdException("Verify nonce failed.")

    storeNonce(nonce, nonceTime + TWO_HOUR)
  }

  def isNonceExist(nonce: String): Boolean = {
    nonces.contains(nonce)
  }

  def storeNonce(nonce: String, expires: Long) {
    nonces += nonce
  }

  def getNonceTime(nonce: String) =
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
      .parse(nonce.substring(0, 19) + "+0000")
      .getTime()
}

class RequestWrapper(request: Request[AnyContent]) {

  def getParameter(name: String) = {
    request.queryString.get(name) match {
      case Some(Seq(p: String)) => p
      case _ => null
    }
  }
}
