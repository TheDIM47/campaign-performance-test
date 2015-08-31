import java.util.Calendar

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.conversions.scala._
RegisterConversionHelpers()

case class Target(target: String, attrList: Seq[String])
case class Campaign(campaignName: String, price: Double, targetList: Seq[Target])
case class User(user: String, profile: Map[String, String])

val t1 = Target("attr_A", List("A1", "B1", "C1"))
val t2 = Target("attr_B", List("A2", "B2", "C2"))
val t3 = Target("attr_C", List("A3", "B3", "C3"))
val campaign = Campaign("cmp004", 163.45, Seq(t1, t2, t3))
val user = User("u3", Map("attr_A" -> "A3", "attr_B" -> "B3"))

implicit def TargetAsDBObject(t: Target): MongoDBObject = {
  MongoDBObject("target" -> t.target, "attr_list" -> t.attrList)
}
implicit def CampaignAsDBObject(c: Campaign): MongoDBObject = {
  MongoDBObject("target" -> c.campaignName, "price" -> c.price, "target_list" -> c.targetList.map(x => TargetAsDBObject(x)))
}
val mongoHost: String = "localhost"
val mongoPort: Int = 27017
println("Mongo started")
val campaigns = List(campaign) // Generator.genCampaign(1, 1, 1)
val dbo = campaigns.map(CampaignAsDBObject(_))
val mongo: MongoClient = MongoClient(host = mongoHost, port = mongoPort)
val db = mongo.getDB("test")
val coll = db.apply("camapigns")
//coll.drop()
//val r1 = coll.insert(dbo:_*)
//coll.createIndex(MongoDBObject("price" -> 1))
//coll.createIndex(MongoDBObject("target_list.target" -> 1))
//coll.createIndex(MongoDBObject("target_list.attr_list" -> 1))
val r2 = coll.count()
val r3 = coll.find().toList.mkString("\n")
val r4 = coll.find("target" $eq "cmp").toList.mkString("\n")
//- the user’s profile “key” matched all the target “value” in the campaigns target list
val q1 = "target_list.target" $all (user.profile.keys)
val q2 = "target_list.attr_list" $all (user.profile.values)
val q3 = $and(q1, q2)
val aggr = MongoDBObject("$group" -> MongoDBObject("price" -> -1, "last" -> MongoDBObject("$max" -> "$price")))
val r5 = coll.find(q1).toList.mkString("\n")
//- the user’s profile “value” is in that target attr_list.
val r6 = coll.find(q2).toList.mkString("\n")
val q4 = List(
  MongoDBObject("$match" -> q3),
  MongoDBObject("$sort" -> MongoDBObject("price" -> -1)),
  MongoDBObject("$limit" -> 1)
)
val q4s = q4.toString
val start = Calendar.getInstance.getTimeInMillis
val r7 = coll.aggregate(q4).results.toList.mkString("\n")
val delta = Calendar.getInstance.getTimeInMillis - start

