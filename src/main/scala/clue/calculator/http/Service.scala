package clue.calculator.http

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import clue.calculator.models.{AverageCycle, Event}

import scala.concurrent.ExecutionContext

class Service(implicit val actorSystem: ActorSystem, val ec: ExecutionContext)
  extends Directives with SprayJsonSupport {

  def routes: Route =
    path("events") {
      post {
        entity(as[Event]) { event =>
          println(s"Received $event")
          complete(OK)
        }
      }
    } ~
      path("cycles" / "average") {
        get {
          complete(OK, AverageCycle())
        }
      }
}