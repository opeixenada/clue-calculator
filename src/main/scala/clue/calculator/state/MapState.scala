package clue.calculator.state

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit.DAYS

class MapState extends State {

  private var state = InternalState()

  /** Register new bleeding event */
  override def addBleedingEvent(userId: Int, timestamp: ZonedDateTime): Unit = {
    val newState = state.usersState(userId) match {
      case s@UserState(None, _, _, _) =>
        // first ever bleeding symptom
        val newUserState = s.copy(lastPeriodStart = Some(timestamp), lastPeriodTracked = Some(timestamp))
        state.update(userId, newUserState)

      case s@UserState(Some(_), Some(lastPeriodTracked), _, _)
        if !timestamp.isAfter(lastPeriodTracked.plusDays(1)) =>
        // consecutive bleeding
        val newUserState = s.copy(lastPeriodTracked = Some(timestamp))
        state.update(userId, newUserState)

      case UserState(Some(lastPeriodStart), Some(_), cyclesCount, cyclesLengthSum) =>
        // new period
        val cycleLength = DAYS.between(lastPeriodStart, timestamp)
        val newUserState = UserState(Some(timestamp), Some(timestamp), cyclesCount + 1, cyclesLengthSum + cycleLength)
        state.update(userId, newUserState, Some(cycleLength))
    }

    state = newState
  }

  /** Retrieve average cycle length for all the users */
  override def getAverageCycleLength: Double = state.getAverageCycleLength

}
