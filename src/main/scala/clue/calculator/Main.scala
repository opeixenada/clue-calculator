package clue.calculator

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import clue.calculator.http.Service

import scala.concurrent.{ExecutionContext, Future}

/**
  * Service that receives symptom events and uses them to determine the running average menstrual cycle length
  * of all users.
  */
object Main extends App {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = actorSystem.dispatcher

  val service = new Service()

  val host = "0.0.0.0"

  val port = 9000

  val apiFuture = Http().bindAndHandle(service.routes, host, port)

  def exitOnFailure(futures: Future[_]*)(implicit ec: ExecutionContext): Unit = {
    for (f <- futures) f.failed.foreach { t =>
      println("Fatal error, exiting", t)
      System.exit(1)
    }
  }

  sys.addShutdownHook {
    apiFuture
      .flatMap(_.unbind())
      .onComplete(_ => actorSystem.terminate())
  }

  exitOnFailure(apiFuture)
}