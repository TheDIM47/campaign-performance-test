import java.util.Calendar

import com.mongodb.casbah.Imports._
import generators.Generator

val mongoHost: String = "localhost"
val mongoPort: Int = 27017
val mongo: MongoClient = MongoClient(host = mongoHost, port = mongoPort)
val db = mongo.getDB("test")
val coll = db.apply("camapigns")
coll.find().mkString("\n")
coll.indexInfo.mkString("\n")
val user = Generator.genUser(120)
val q1 = "target_list.target" $eq "attr_A"
val q2 = "target_list.attr_list" $elemMatch(MongoDBObject("$eq" -> "A5"))
coll.find(q1 ++ q2).mkString("\n")
val q3 = $and(q1, q2)
coll.find(q3).mkString("\n")
//q3.toString
//val q4 = List(
//  MongoDBObject("$match" -> q3),
//  MongoDBObject("$sort" -> MongoDBObject("price" -> -1)),
//  MongoDBObject("$limit" -> 1)
//)
//q4.toString
//
////val e5 = coll.aggregate(q4).underlying
//val start = Calendar.getInstance.getTimeInMillis
//val r4 = coll.find(q3).limit(1).underlying.hasNext
//if (r4) {
//  val r5 = coll.aggregate(q4).results.headOption
//}
//val time = Calendar.getInstance.getTimeInMillis - start
////val r6 = coll.find(q4).limit(5).toList.mkString("\n")
