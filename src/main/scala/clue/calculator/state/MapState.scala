package clue.calculator.state

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit.DAYS

class MapState extends State {

  private var usersState: Map[Int, UserState] = Map.empty.withDefaultValue(UserState())

  /** Register new bleeding event */
  override def addBleedingEvent(userId: Int, timestamp: ZonedDateTime): Unit = {
    val newUserState = usersState(userId) match {
      case s@UserState(None, _, _, _) =>
        // first ever bleeding symptom
        s.copy(lastPeriodStart = Some(timestamp), lastPeriodTracked = Some(timestamp))

      case s@UserState(Some(_), Some(lastPeriodTracked), _, _)
        if !timestamp.isAfter(lastPeriodTracked.plusDays(1)) =>
        // consecutive bleeding
        s.copy(lastPeriodTracked = Some(timestamp))

      case UserState(Some(lastPeriodStart), Some(_), cyclesCount, cyclesLengthSum) =>
        // new period
        val cycleLength = DAYS.between(lastPeriodStart, timestamp)
        UserState(Some(timestamp), Some(timestamp), cyclesCount + 1, cyclesLengthSum + cycleLength)
    }
    usersState = usersState + (userId -> newUserState)
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
