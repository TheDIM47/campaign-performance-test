package objects

import org.json4s.JsonFormat
import spray.http.HttpRequest
import spray.httpx.SprayJsonSupport
import spray.httpx.unmarshalling.FromRequestUnmarshaller

/** Campaign */

case class Target(target: String, attrList: Seq[String])

case class Campaign(name: String, price: Double, targets: Seq[Target])

/** User */

case class User(user: String, profile: Map[String, String])
