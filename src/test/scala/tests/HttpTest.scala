package tests

import java.util.Calendar

import akka.actor.ActorRef
import akka.util.Timeout
import app.SearchActor
import org.scalatest.prop.Configuration
import org.scalatest.{Matchers, FlatSpec}
import scala.language.existentials
import spray.http.MediaTypes
import spray.http.StatusCodes._
import spray.routing.HttpService
import spray.testkit.ScalatestRouteTest

trait TestService extends FlatSpec with ScalatestRouteTest with HttpService {
  implicit def actorRefFactory = system

  lazy implicit val executionContext = actorRefFactory.dispatcher

  import akka.pattern.ask
  import scala.concurrent.duration._

  val MongoHost = "localhost"
  val MongoPort = 27017
  val searchActor: ActorRef = system.actorOf(SearchActor.props(MongoHost, MongoPort))

  val searchRoot = respondWithMediaType(MediaTypes.`application/json`) {
    path(IntNumber) { r =>
      get {
        complete {
          implicit val timeout = Timeout(100.seconds)
          (searchActor ? r).mapTo[String] //.map(Converters.toJson(_))
        }
      }
    }
  }
}

class HttpTest extends TestService with Matchers {
  val count = 10000
  "Request" should "return some response for sequental GET requests to /N" in {
    var minTime = Long.MaxValue
    var maxTime = Long.MinValue
    var sum:Long = 0
    for(i <- (1 to count)) {
      val start = Calendar.getInstance.getTimeInMillis
      Get(s"/$i") ~> searchRoot ~> check {
        assert(response.status == OK)
      }
      val time = Calendar.getInstance.getTimeInMillis - start
      minTime = Math.min(minTime, time)
      maxTime = Math.max(maxTime, time)
      sum += time
    }
    println(s"min: $minTime max: $maxTime avg: ${sum/count.toDouble} total: $sum count: $count")
  }

  "Request" should "return some response for random GET requests to /N" in {
    var minTime = Long.MaxValue
    var maxTime = Long.MinValue
    var sum:Long = 0
    for(k <- (1 to count)) {
      val i: Long = Math.round(Math.random() * count)
      val start = Calendar.getInstance.getTimeInMillis
      Get(s"/$i") ~> searchRoot ~> check {
        assert(response.status == OK)
      }
      val time = Calendar.getInstance.getTimeInMillis - start
      minTime = Math.min(minTime, time)
      maxTime = Math.max(maxTime, time)
      sum += time
    }
    println(s"min: $minTime max: $maxTime avg: ${sum/count.toDouble} total: $sum count: $count")
  }
}
