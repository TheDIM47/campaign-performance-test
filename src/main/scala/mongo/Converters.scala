package mongo

import app.CreateActor.CreateDatabaseResponse
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

object Converters {

  import com.mongodb.casbah.Imports._
  import objects._

  implicit def TargetAsDBObject(t: Target): MongoDBObject = {
    MongoDBObject("_id" -> t.target, "attr_list" -> t.attrList)
  }

  implicit def CampaignAsDBObject(c: Campaign): MongoDBObject = {
    MongoDBObject("_id" -> c.campaignName, "price" -> c.price, "target_list" -> c.targetList.map(x => TargetAsDBObject(x)))
  }

  implicit val formats = Serialization.formats(NoTypeHints)

  def toJson(s: Campaign): String = writePretty(s)

  def toJson(xs: Seq[Campaign]): String = writePretty(xs)

  def toJson(s: CreateDatabaseResponse): String = writePretty(s)
}
