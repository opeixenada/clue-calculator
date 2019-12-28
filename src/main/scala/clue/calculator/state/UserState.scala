package clue.calculator.state

import java.time.ZonedDateTime

case class UserState(lastPeriodStart: Option[ZonedDateTime] = None,
                     lastPeriodTracked: Option[ZonedDateTime] = None,
                     cyclesCount: Int = 0,
                     cyclesLengthSum: Long = 0)