package app

import java.util.Calendar

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import generators.Generator

object CreateActor {
  case class CreateDataRequest(numberOfCampaigns: Int, targetList: Int, attributes: Int)
  def props(host: String, port: Int): Props = Props(classOf[CreateActor], host, port)
}

class CreateActor(host: String, port: Int) extends Actor with ActorLogging {
  import app.CreateActor._
  override def receive: Receive = {
    case CreateDataRequest(numberOfCampaigns, targetList, attributes) => {
      val start = Calendar.getInstance.getTimeInMillis
      val campaigns = Generator.genCampaign(numberOfCampaigns, targetList, attributes)
      val t = Calendar.getInstance.getTimeInMillis - start
      log.info(s"[${campaigns.size}] campaigns created for [$t] msecs. Requested [$numberOfCampaigns] campaigns")
      sender ! campaigns
      self ! PoisonPill
    }
  }
}
