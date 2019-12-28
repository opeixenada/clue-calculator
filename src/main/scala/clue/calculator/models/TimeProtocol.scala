package clue.calculator.models

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import spray.json.{JsString, JsValue, JsonFormat, deserializationError}

/** Serialization protocol for timestamps */
trait TimeProtocol {

  implicit object DefaultTimeFormat extends JsonFormat[ZonedDateTime] {

    override def write(dateTime: ZonedDateTime): JsValue = {
      JsString(dateTime.format(DateTimeFormatter.ISO_DATE_TIME))
    }

    override def read(json: JsValue): ZonedDateTime = {
      json match {
        case JsString(s) => ZonedDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME)
        case _ => deserializationError("Expected timestamp as a string in ISO format")
      }
    }
  }

}

object TimeProtocol extends TimeProtocol