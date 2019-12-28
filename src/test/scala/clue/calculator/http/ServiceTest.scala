package clue.calculator.http

import java.time.ZonedDateTime

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route.seal
import akka.http.scaladsl.testkit.ScalatestRouteTest
import clue.calculator.models.Event
import clue.calculator.models.Symptom._
import clue.calculator.state.State
import org.mockito.{ArgumentMatchersSugar, MockitoSugar}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.DefaultJsonProtocol._
import spray.json.{JsNumber, JsObject}

class ServiceTest
  extends AnyWordSpec
    with Matchers
    with ScalatestRouteTest
    with MockitoSugar
    with ArgumentMatchersSugar {

  trait BasicScenario {

    val ts: ZonedDateTime = ZonedDateTime.now()

    val state: State = mock[State]
    doNothing.when(state).addBleedingEvent(any[Int], any[ZonedDateTime])
    when(state.getAverageCycleLength).thenReturn(0)

    val service = new Service(state)

  }

  "/events" must {
    "accept a valid symptom event" in new BasicScenario {
      private val validEvent =
        s"""
           |{
           |  "user_id": 123456,
           |  "symptom": 3,
           |  "timestamp": "2017-04-23T18:25:43.511Z"
           |}""".stripMargin


      Post(s"/events", HttpEntity(ContentTypes.`application/json`, validEvent)) ~>
        seal(service.routes) ~>
        check {
          status mustBe StatusCodes.NoContent
        }
    }

    "not accept an invalid symptom event" in new BasicScenario {
      private val invalidEvent = "{}"

      Post(s"/events", HttpEntity(ContentTypes.`application/json`, invalidEvent)) ~>
        seal(service.routes) ~>
        check {
          status mustBe StatusCodes.BadRequest
        }
    }

    "not accept an invalid symptom ID" in new BasicScenario {
      private val validEvent =
        s"""
           |{
           |  "user_id": 123456,
           |  "symptom": 7,
           |  "timestamp": "2017-04-23T18:25:43.511Z"
           |}""".stripMargin


      Post(s"/events", HttpEntity(ContentTypes.`application/json`, validEvent)) ~>
        seal(service.routes) ~>
        check {
          status mustBe StatusCodes.BadRequest
        }
    }
  }

  "/cycles/average" must {
    "return a valid object" in new BasicScenario {
      Get(s"/cycles/average") ~>
        seal(service.routes) ~>
        check {
          status mustBe StatusCodes.OK
          responseAs[JsObject] mustBe JsObject("average_cycle" -> JsObject("length" -> JsNumber(0)))
        }
    }
  }

  "Service" must {
    "ignore non-bleeding events" in new BasicScenario {
      Post(s"/events", Event(0, IncreasedFocus, ts)) ~> seal(service.routes)
      Post(s"/events", Event(0, Cramps, ts)) ~> seal(service.routes)
      Post(s"/events", Event(0, TenderBreasts, ts)) ~> seal(service.routes)

      verify(state, never).addBleedingEvent(0, ts)
    }

    "add bleeding events" in new BasicScenario {
      Post(s"/events", Event(0, LightBleeding, ts)) ~> seal(service.routes)
      Post(s"/events", Event(0, MediumBleeding, ts)) ~> seal(service.routes)
      Post(s"/events", Event(0, HeavyBleeding, ts)) ~> seal(service.routes)

      verify(state, times(3)).addBleedingEvent(0, ts)
    }
  }

}
