package app

import java.util.Calendar

import akka.actor.{PoisonPill, Actor, ActorLogging, Props}
import com.mongodb.casbah.Imports._
import generators.Generator
import objects.User

object SearchActor {
//  def props(host: String, port: Int): Props = Props(classOf[SearchActor], host, port)
  def props(coll:MongoCollection): Props = Props(classOf[SearchActor], coll)
}

class SearchActor(coll:MongoCollection)
// (host: String, port: Int)
  extends Actor with ActorLogging {
  val NO_RESULTS = "{}"
//  val mongo: MongoClient = MongoClient(host = host, port = port)
//  val db = mongo.getDB("test")
//  val coll:MongoCollection = db.apply("camapigns")

  override def receive: Receive = {
    case user: User => {
      log.debug(s"User: $user")
      val qs = ("target_list.target" $in user.profile.keys.seq) ++
          ("target_list.attr_list" $elemMatch(MongoDBObject("$in" -> user.profile.values.seq)))
      val count = coll.find(qs).limit(1)
      if (count.underlying.hasNext) {
        val q4 = List(
          MongoDBObject("$match" -> qs),
          MongoDBObject("$sort" -> MongoDBObject("price" -> -1)),
          MongoDBObject("$limit" -> 1)
        )
        val searchResult = coll.aggregate(q4).results.head.toString
        sender ! searchResult
      } else {
        sender ! NO_RESULTS
      }
      self ! PoisonPill
    }
    case x => {
      log.error(s"Invalid User ID [$x] from $sender")
      self ! PoisonPill
    }
  }

//  @throws[Exception](classOf[Exception])
//  override def postStop(): Unit = {
//    mongo.close()
//  }
}
