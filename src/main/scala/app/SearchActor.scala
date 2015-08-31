package app

import java.util.Calendar

import akka.actor.{Actor, ActorLogging, Props}
import com.mongodb.casbah.Imports._
import generators.Generator

object SearchActor {
  def props(host: String, port: Int): Props = Props(classOf[SearchActor], host, port)
}

class SearchActor(host: String, port: Int) extends Actor with ActorLogging {
  val NO_RESULTS = "{}"
  val mongo: MongoClient = MongoClient(host = host, port = port)
  val db = mongo.getDB("test")
  val coll = db.apply("camapigns")

  override def receive: Receive = {
    case r: Int => {
      val user = Generator.genUser(r)
      log.debug(s"User: $user")
      val q1 = "target_list._id" $eq user.profile.keys.toList.sorted.last
      val q2 = "target_list.attr_list" $elemMatch(MongoDBObject("$eq" -> user.profile.values.toList.sorted.last))
      val q3 = $and(q1, q2)
//      val start = Calendar.getInstance.getTimeInMillis
//{
//        val count = coll.find(q3).limit(1)
//        val stop = Calendar.getInstance.getTimeInMillis
//        log.debug(s"Count search time: ${stop - start}")
//        count
//      }
      val count = coll.find(q3).limit(1)
      if (count.underlying.hasNext) {
        val q4 = List(
          MongoDBObject("$match" -> q3),
          MongoDBObject("$sort" -> MongoDBObject("price" -> -1)),
          MongoDBObject("$limit" -> 1)
        )
//        val start = Calendar.getInstance.getTimeInMillis
        val searchResult = coll.aggregate(q4).results.headOption
//        val stop = Calendar.getInstance.getTimeInMillis
        searchResult match {
          case Some(x) => sender ! x.toString
          case None => sender ! NO_RESULTS
        }
//        log.debug(s"Search time: ${stop - start}")
      } else {
        sender ! NO_RESULTS
      }
    }
    case x => log.error(s"Invalid User ID [$x] from $sender")
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    mongo.close()
  }
}
