package app

import akka.actor.{ActorRef, ActorSystem}
import akka.util.Timeout
import app.CreateActor.{CreateDatabaseResponse, CreateDatabaseRequest}
import mongo.Converters
import objects.Campaign
import spray.http.MediaTypes
import spray.routing.SimpleRoutingApp

object CampaignServer extends App with SimpleRoutingApp {
  import akka.pattern.ask
  import akka.actor.{Actor, Props, ActorSystem}
  import scala.concurrent.duration._

  implicit val system = ActorSystem()
  import system.dispatcher

  val MongoHost = "localhost"
  val MongoPort = 27017
  val searchActor: ActorRef = system.actorOf(SearchActor.props(MongoHost, MongoPort))

  def searchRoot = path(IntNumber) { r =>
    respondWithMediaType(MediaTypes.`application/json`) {
      complete {
        implicit val timeout = Timeout(100.seconds)
        (searchActor ? r).mapTo[String]//.map(Converters.toJson(_))
      }
    }
  }

  def createRoot = path("create") {
    complete {
      val createActor: ActorRef = system.actorOf(CreateActor.props)
      implicit val timeout = Timeout(600.seconds)
      (createActor ? CreateDatabaseRequest(MongoHost, MongoPort)).mapTo[CreateDatabaseResponse].map(Converters.toJson(_))
    }
  }

  startServer(interface = "localhost", port = 9080) {
    get {
      searchRoot ~ createRoot
    }
  }
}
