package mongo

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

object Converters {

  import com.mongodb.casbah.Imports._
  import scala.language.implicitConversions
  import objects._

  implicit def TargetAsDBObject(t: Target): MongoDBObject = {
    MongoDBObject("target" -> t.target, "attr_list" -> t.attrList)
  }

  implicit def CampaignAsDBObject(c: Campaign): MongoDBObject = {
    MongoDBObject("campaign_name" -> c.name, "price" -> c.price, "target_list" -> c.targets.map(x => TargetAsDBObject(x)))
  }

  implicit val formats = Serialization.formats(NoTypeHints)

  def toJson(s: Campaign): String = writePretty(s)

  def toJson(s: Option[Campaign]): String = s match {
    case Some(x) => writePretty(x)
    case _ => "{}"
  }

  def toJson(xs: Seq[Campaign]): String = write(xs)

  def toJson(u: User): String = writePretty(u)
}
