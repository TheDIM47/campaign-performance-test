package app

import java.util.concurrent.atomic.AtomicLong

import akka.actor.{ActorRef, ActorSystem}
import akka.util.{ByteString, Timeout}
import app.CreateActor.{CreateDataRequest}
import com.mongodb.DBObject
import com.mongodb.casbah.{MongoDB, MongoClient, MongoCollection}
import generators.Generator
import mongo.Converters
import objects.{Target, User, Campaign}
import spray.http.MediaTypes
import spray.json.DefaultJsonProtocol
import spray.routing.SimpleRoutingApp

import spray.httpx.SprayJsonSupport._
object UserJsonProtocol extends DefaultJsonProtocol{
  implicit val userFormat = jsonFormat2(User)
  implicit val targetFormat = jsonFormat2(Target)
  implicit val campaignFormat = jsonFormat3(Campaign)
}

// http://104.236.14.157:9080/create
object CampaignServer extends App with SimpleRoutingApp {

  import akka.pattern.ask
  import akka.actor.{Actor, Props, ActorSystem}
  import scala.concurrent.duration._

  implicit val system = ActorSystem()

  import system.dispatcher

  val MaxCampaigns = "10000"
  val MaxTargets = "26"
  val MaxAttributes = "100"

  val MongoHost = "localhost"
  val MongoPort = 27017

  val mongo: MongoClient = MongoClient(host = MongoHost, port = MongoPort)
  val db:MongoDB = mongo.getDB("test")
  val coll:MongoCollection = db.apply("camapigns")

  var userCounter: Int = 0

  // GET http://myhost:3000/search_auto
  def searchAutoRoot = path("search_auto") {
    get {
      respondWithMediaType(MediaTypes.`application/json`) {
        complete {
          userCounter += 1
          val user = Generator.genUser(userCounter)
//          val searchActor: ActorRef = system.actorOf(SearchActor.props(MongoHost, MongoPort))
          val searchActor: ActorRef = system.actorOf(SearchActor.props(coll))
//          val searchActor: ActorRef = system.actorOf(SearchActor.props(db))
          ((searchActor ? user)(Timeout(6.seconds))).mapTo[String] //[Option[DBObject]].map(_.toString)
        }
      }
    }
  }

  // GET http://myhost:3000/search_random
  def searchRandomRoot = path("search_random") {
    get {
      respondWithMediaType(MediaTypes.`application/json`) {
        complete {
          val num = Math.round(1000 * Math.random()).toInt
          val user = Generator.genUser(num)
          val searchActor: ActorRef = system.actorOf(SearchActor.props(coll))
          ((searchActor ? user)(Timeout(6.seconds))).mapTo[String] //[Option[DBObject]].map(_.toString)
        }
      }
    }
  }

  // POST http://myhost:3000/search
  def searchRoot = path("search") {
    post {
      import UserJsonProtocol._
      entity(as[User]) { user =>
        respondWithMediaType(MediaTypes.`application/json`) {
          complete {
//            val searchActor: ActorRef = system.actorOf(SearchActor.props(MongoHost, MongoPort))
            val searchActor: ActorRef = system.actorOf(SearchActor.props(coll))
//            val searchActor: ActorRef = system.actorOf(SearchActor.props(db))
            ((searchActor ? user)(Timeout(6.seconds))).mapTo[String] //[Option[DBObject]].map(_.toString)
          }
        }
      }
    }
  }

  // GET http://myhost:3000/user - generate next user
  // GET http://myhost:3000/user?n={number} - get user with N
  def createUserRoot = path("user") {
    get {
      parameters('n.?) { n =>
        respondWithMediaType(MediaTypes.`application/json`) {
          val number: Int = n match {
            case None =>
              userCounter += 1
              userCounter
            case Some(x) =>
              x.toInt
          }
          complete {
            Converters.toJson(Generator.genUser(number))
          }
        }
      }
    }
  }

  // GET http://myhost:3000/campaign?x={number}&y={number}&z={number}
  // The generator has parameters: X (X <= 100) , Y (Y =< 26), Z (Z <= 10000)
  def createDataRoot = path("campaign") {
    get {
      parameters('x.?, 'y.?, 'z.?) { (xa, ya, za) =>
        complete {
          val attributes = xa.getOrElse(MaxAttributes).toInt
          val targetList = ya.getOrElse(MaxTargets).toInt
          val numberOfCampaigns = za.getOrElse(MaxCampaigns).toInt
          val createActor: ActorRef = system.actorOf(CreateActor.props(MongoHost, MongoPort))
          val msg = CreateDataRequest(numberOfCampaigns, targetList, attributes)
          ((createActor ? msg)(60.seconds)).mapTo[Seq[Campaign]].map(x => Converters.toJson(x))
        }
      }
    }
  }

  // POST http://myhost:3000/import_camp
  def importRoot = path("import_camp") {
    post {
      import UserJsonProtocol._
      entity(as[Seq[Campaign]]) { campaigns =>
        val importActor: ActorRef = system.actorOf(ImportActor.props(MongoHost, MongoPort, campaigns))
        complete {
          (importActor ? "")(60.seconds).mapTo[String]
        }
      }
    }
  }

  // GET http://myhost:3000/empty
  def emptyRoot = path("empty") {
    get {
      respondWithMediaType(MediaTypes.`application/json`) {
        complete {
          val emptyActor: ActorRef = system.actorOf(EmptyActor.props)
          (emptyActor ? "None")(60.seconds).mapTo[String]
        }
      }
    }
  }

  def countingRoot = path("counting") {
    get {
        complete {
          counter.incrementAndGet.toString
        }
    }
  }

  private val counter: AtomicLong = new AtomicLong(0L)

  startServer(interface = "localhost", port = 9080) {
    countingRoot ~ emptyRoot ~ searchRoot ~ searchAutoRoot ~ searchRandomRoot ~ createDataRoot ~ createUserRoot ~ importRoot
  }
}
