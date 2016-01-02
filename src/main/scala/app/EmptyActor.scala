package app

import akka.actor.Actor.Receive
import akka.actor.{Props, PoisonPill, Actor, ActorLogging}

object EmptyActor {
  def props: Props = Props(classOf[EmptyActor])
}

class EmptyActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case "None" => {
      sender ! "Echo"
      self ! PoisonPill
    }
  }
}
