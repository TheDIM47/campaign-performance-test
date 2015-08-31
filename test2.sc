import java.util.Calendar

import com.mongodb.casbah.Imports._
import generators.Generator

val mongoHost: String = "localhost"
val mongoPort: Int = 27017
val mongo: MongoClient = MongoClient(host = mongoHost, port = mongoPort)
val db = mongo.getDB("test")
val coll = db.apply("camapigns")
coll.dropIndexes
coll.createIndex(MongoDBObject("price" -> -1))
//coll.createIndex(MongoDBObject("target_list.attr_list" -> 1, "target_list._id" -> 1))
//coll.createIndex(MongoDBObject("target_list._id" -> 1, "target_list.attr_list" -> 1))
coll.createIndex(MongoDBObject("target_list._id" -> 1))
coll.createIndex(MongoDBObject("target_list.attr_list" -> 1))
//coll.createIndex(MongoDBObject("target_list" -> 1))
coll.indexInfo.mkString("\n")
val user = Generator.genUser(120)
user.profile.keys.toList.sorted.last
user.profile.values.toList.sorted.last
//val q1 = "target_list._id" $all user.profile.keys.toList.sorted
val q1 = "target_list._id" $eq user.profile.keys.toList.sorted.last
val q2 = "target_list.attr_list" $elemMatch(MongoDBObject("$eq" -> user.profile.values.toList.sorted.last))
val q3 = $and(q1, q2)
val q4 = List(
  MongoDBObject("$match" -> q3),
  MongoDBObject("$sort" -> MongoDBObject("price" -> -1)),
  MongoDBObject("$limit" -> 1)
)

//val e5 = coll.aggregate(q4).underlying
val start = Calendar.getInstance.getTimeInMillis
val r4 = coll.find(q3).limit(1).underlying.hasNext
if (r4) {
  val r5 = coll.aggregate(q4).results.headOption
}
val time = Calendar.getInstance.getTimeInMillis - start
//val r6 = coll.find(q4).limit(5).toList.mkString("\n")
