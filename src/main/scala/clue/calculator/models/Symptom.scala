package clue.calculator.models

import java.util.NoSuchElementException

import spray.json.{DeserializationException, JsNumber, JsValue, JsonFormat}

object Symptom extends Enumeration {
  type Symptom = Value

  val LightBleeding = Value(1)
  val MediumBleeding = Value(2)
  val HeavyBleeding = Value(3)
  val IncreasedFocus = Value(4)
  val Cramps = Value(5)
  val TenderBreasts = Value(6)

  def isBleeding: Symptom => Boolean = Seq(LightBleeding, MediumBleeding, HeavyBleeding).contains

  implicit val fmt: JsonFormat[Symptom] = new JsonFormat[Symptom] {

    override def write(obj: Symptom): JsValue = JsNumber(obj.id)

    override def read(json: JsValue): Symptom = {
      json match {
        case JsNumber(id) if id.isValidInt => try {
          Symptom(id.toInt)
        } catch {
          case _: NoSuchElementException => throw DeserializationException("Invalid symptom ID")
        }
        case _ => throw DeserializationException("Invalid symptom ID")
      }
    }

  }
}