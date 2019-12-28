package clue.calculator.models

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

/** Representation of a running average cycle of all users */
case class AverageCycle(average_cycle: Length = Length(0))

object AverageCycle {
  implicit val fmt: RootJsonFormat[AverageCycle] = jsonFormat1(AverageCycle.apply)
}

case class Length(length: Double)

object Length {
  implicit val fmt: RootJsonFormat[Length] = jsonFormat1(Length.apply)
}