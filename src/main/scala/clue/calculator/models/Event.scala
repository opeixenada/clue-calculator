package clue.calculator.models

import java.time.ZonedDateTime

import clue.calculator.models.TimeProtocol._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

/** Event that is emitted when user records a new symptom */
case class Event(user_id: Int, symptom: Int, timestamp: ZonedDateTime)

object Event {
  implicit val fmt: RootJsonFormat[Event] = jsonFormat3(Event.apply)
}