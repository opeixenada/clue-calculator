package clue.calculator.state

import java.time.temporal.ChronoUnit.DAYS

import akka.Done
import clue.calculator.models.{Event, isBleeding}

import scala.concurrent.Future

class MapState extends State {

  private var usersState: Map[Int, UserState] = Map.empty.withDefaultValue(UserState())

  /** Register new symptom event */
  override def addEvent(event: Event): Unit = {
    if (!isBleeding(event.symptom)) {
      // ignore all the symptoms that are not bleeding
      Future.successful(Done)
    }
    else {
      val newUserState = usersState(event.user_id) match {
        case s@UserState(None, _, _, _) =>
          // first ever bleeding symptom
          s.copy(lastPeriodStart = Some(event.timestamp), lastPeriodTracked = Some(event.timestamp))

        case s@UserState(Some(_), Some(lastPeriodTracked), _, _)
          if !event.timestamp.isAfter(lastPeriodTracked.plusDays(1)) =>
          // consecutive bleeding
          s.copy(lastPeriodTracked = Some(event.timestamp))

        case UserState(Some(lastPeriodStart), Some(_), cyclesCount, cyclesLengthSum) =>
          // new period
          val cycleLength = DAYS.between(lastPeriodStart, event.timestamp)
          UserState(Some(event.timestamp), Some(event.timestamp), cyclesCount + 1, cyclesLengthSum + cycleLength)
      }
      usersState = usersState + (event.user_id -> newUserState)
    }
  }

  /** Retrieve average cycle length for all the users */
  override def getAverageCycleLength: Double = {
    val (cyclesCountAllUsers, cyclesLengthSumAllUsers) = usersState.values.foldLeft((0: Int, 0: Long)) {
      case ((cyclesCount, cyclesLengthSum), userState) =>
        (cyclesCount + userState.cyclesCount, cyclesLengthSum + userState.cyclesLengthSum)
    }
    if (cyclesCountAllUsers > 0) cyclesLengthSumAllUsers.toDouble / cyclesCountAllUsers else 0
  }
}
