package app

import java.util.Calendar

import akka.actor.{PoisonPill, Props, Actor, ActorLogging}
import com.mongodb.casbah.Imports._
import mongo.Converters
import objects.Campaign

object ImportActor {
  def props(host: String, port: Int, campaigns: Seq[Campaign]): Props = Props(classOf[ImportActor], host, port, campaigns)
}

class ImportActor(host: String, port: Int, campaigns: Seq[Campaign]) extends Actor with ActorLogging {
  override def receive: Receive = {
    case _ => {
      val start = Calendar.getInstance.getTimeInMillis
      val client = MongoClient(host = host, port = port)
      try {
        log.info("DB connected")
        val db = client.getDB("test")
        val coll = db.apply("camapigns")
        coll.dropIndexes()
        coll.drop()
        log.info("Old collection dropped")
        coll.insert(campaigns.map(x => Converters.CampaignAsDBObject(x)) :_*)
        log.info("Insert completed")
        coll.createIndex(MongoDBObject("price" -> -1))
        coll.createIndex(MongoDBObject("target_list.target" -> 1))
        coll.createIndex(MongoDBObject("target_list.attr_list" -> 1))
//        coll.createIndex(MongoDBObject("target_list.target" -> 1, "target_list.attr_list" -> 1))
        log.info("Indexing completed")
        val t = Calendar.getInstance.getTimeInMillis - start
        sender ! s"Upload [${campaigns.size}] completed for $t msecs"
      } catch {
        case e: Throwable => sender ! s"Upload failed with ${e.getMessage}"
      } finally {
        client.close()
        log.info("DB closed")
      }
      self ! PoisonPill
    }
  }
}
