package clue.calculator.state

import java.time.ZonedDateTime

/** State kept for each user */
case class UserState(lastPeriodStart: Option[ZonedDateTime] = None,
                     lastPeriodTracked: Option[ZonedDateTime] = None)