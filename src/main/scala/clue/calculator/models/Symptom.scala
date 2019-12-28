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

  def isBleeding: Symptom.Value => Boolean = Seq(LightBleeding, MediumBleeding, HeavyBleeding).contains

  implicit val fmt: JsonFormat[Symptom.Value] = new JsonFormat[Symptom.Value] {

    override def write(obj: Symptom.Value): JsValue = JsNumber(obj.id)

    override def read(json: JsValue): Symptom.Value = {
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