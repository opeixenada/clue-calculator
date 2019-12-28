package clue.calculator.state

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit.DAYS

class InMemoryState extends State {

  private var state = InternalState()

  /** Register new bleeding event */
  override def addBleedingEvent(userId: Int, timestamp: ZonedDateTime): Unit = {
    val newState = state.usersState(userId) match {
      case UserState(None, _) =>
        // first ever bleeding symptom
        val newUserState = UserState(Some(timestamp), Some(timestamp))
        state.update(userId, newUserState)

      case s@UserState(Some(_), Some(lastPeriodTracked))
        if timestamp.isBefore(lastPeriodTracked.plusDays(2)) =>
        // consecutive bleeding
        val newUserState = s.copy(lastPeriodTracked = Some(timestamp))
        state.update(userId, newUserState)

      case UserState(Some(lastPeriodStart), Some(_)) =>
        // new period
        val cycleLength = DAYS.between(lastPeriodStart, timestamp)
        val newUserState = UserState(Some(timestamp), Some(timestamp))
        state.update(userId, newUserState, Some(cycleLength))
    }

    state = newState
  }

  /** Retrieve average cycle length for all the users */
  override def getAverageCycleLength: Double = state.getAverageCycleLength

}
