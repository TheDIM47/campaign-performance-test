package app

import java.util.Calendar

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import generators.Generator
import mongo.Converters
import objects.Campaign

object CreateActor {

  case class CreateDatabaseRequest(host: String, port: Int)

  case class CreateDatabaseResponse(time: Long, records: Long, error: Option[String])

  def props: Props = Props(classOf[CreateActor])
}

class CreateActor extends Actor with ActorLogging {

  import app.CreateActor.{CreateDatabaseRequest, CreateDatabaseResponse}

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.commons.conversions.scala._
  RegisterConversionHelpers()

  override def receive: Receive = {
    case CreateDatabaseRequest(host, port) => {
      val start = Calendar.getInstance.getTimeInMillis
      val mongo = MongoClient(host = host, port = port)
      log.info("Mongo connected")
      val db = mongo.getDB("test")
      val coll = db.apply("camapigns")
      coll.drop()
      val campaigns: Seq[Campaign] = Generator.genCampaign(c = 10000, t = 26, a = 100)
      val dbObjects = campaigns.map(Converters.CampaignAsDBObject(_))
      log.info(s"Campaign objects created [${dbObjects.size}}]")
      val inserts = coll.insert(dbObjects: _*)
      log.info("Insert completed")
      coll.createIndex(MongoDBObject("price" -> -1))
      coll.createIndex(MongoDBObject("target_list._id" -> 1))
      coll.createIndex(MongoDBObject("target_list.attr_list" -> 1))
      log.info("Indexing completed")
      mongo.close()
      val stop = Calendar.getInstance.getTimeInMillis
      sender ! CreateDatabaseResponse(stop - start, inserts.getN, None)
      //
      self ! PoisonPill
    }
  }
}
