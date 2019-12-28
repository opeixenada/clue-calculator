package clue.calculator.http

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import clue.calculator.models.Event
import clue.calculator.models.Symptom._
import clue.calculator.state.MapState
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MapStateTest extends AnyWordSpec with Matchers {

  private def parseDateTime(s: String) = ZonedDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME)

  "MapState" must {
    "initially set average cycle length to 0" in {
      val state = new MapState()
      state.getAverageCycleLength mustBe 0
    }

    "calculate cycle length with 1-day period" in {
      val state = new MapState()
      state.addEvent(Event(0, LightBleeding, parseDateTime("2019-11-01T00:00:00.000Z")))
      state.addEvent(Event(0, LightBleeding, parseDateTime("2019-12-01T00:00:00.000Z")))
      state.getAverageCycleLength mustBe 30
    }

    "calculate cycle length with 3-days period" in {
      val state = new MapState()
      state.addEvent(Event(0, LightBleeding, parseDateTime("2019-11-01T00:00:00.000Z")))
      state.addEvent(Event(0, LightBleeding, parseDateTime("2019-11-02T00:00:00.000Z")))
      state.addEvent(Event(0, LightBleeding, parseDateTime("2019-11-03T00:00:00.000Z")))
      state.addEvent(Event(0, LightBleeding, parseDateTime("2019-12-01T00:00:00.000Z")))
      state.getAverageCycleLength mustBe 30
    }

    "calculate average cycle length for 2 users" in {
      val state = new MapState()
      state.addEvent(Event(0, LightBleeding, parseDateTime("2019-11-01T00:00:00.000Z")))
      state.addEvent(Event(0, LightBleeding, parseDateTime("2019-12-01T00:00:00.000Z")))
      state.addEvent(Event(1, LightBleeding, parseDateTime("2019-11-01T00:00:00.000Z")))
      state.addEvent(Event(1, LightBleeding, parseDateTime("2019-11-30T00:00:00.000Z")))
      state.getAverageCycleLength mustBe 29.5
    }
  }

}
