package clue.calculator.http

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import clue.calculator.models.{AverageCycle, Event, Length}
import clue.calculator.state.State

import scala.concurrent.ExecutionContext

class Service(state: State)(implicit val actorSystem: ActorSystem, val ec: ExecutionContext)
  extends Directives with SprayJsonSupport {

  def routes: Route =
    path("events") {
      post {
        entity(as[Event]) { event =>
          println(s"Received $event")
          state.addEvent(event)
          complete {
            NoContent
          }
        }
      }
    } ~
      path("cycles" / "average") {
        get {
          complete {
            AverageCycle(Length(state.getAverageCycleLength))
          }
        }
      }
}