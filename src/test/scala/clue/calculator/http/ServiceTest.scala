package clue.calculator.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route.seal
import akka.http.scaladsl.testkit.ScalatestRouteTest
import clue.calculator.models.Event
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
    val state: State = mock[State]
    doNothing.when(state).addEvent(any[Event])
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

}
