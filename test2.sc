import java.util.Calendar

import com.mongodb.casbah.Imports._
import generators.Generator

val mongoHost: String = "localhost"
val mongoPort: Int = 27017
val mongo: MongoClient = MongoClient(host = mongoHost, port = mongoPort)
val db = mongo.getDB("test")
val coll = db.apply("camapigns")
//coll.find().mkString("\n")
coll.indexInfo.mkString("\n")
val user = Generator.genUser(5)
//val qs = ("target_list.target" $in user.profile.keys.seq) ++
//  ("target_list.attr_list" $elemMatch(MongoDBObject("$in" -> user.profile.values.seq))) ++
val qs = MongoDBObject("target_list.target" -> MongoDBObject("$in" -> user.profile.keys.seq),
  "target_list.attr_list" -> MongoDBObject("$elemMatch" -> (MongoDBObject("$in" -> user.profile.values.seq))))
val hints = MongoDBObject("target_list.target" -> 1,
  "target_list.attr_list_1" -> 1)
//coll.setHintFields(hints)
coll.find(qs).hint(MongoDBObject("target_list.attr_list" -> 1)).explain
val start = Calendar.getInstance.getTimeInMillis
coll.find(qs).limit(1)
val stop = Calendar.getInstance.getTimeInMillis - start
