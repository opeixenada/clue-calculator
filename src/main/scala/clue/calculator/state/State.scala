package clue.calculator.state

import java.time.ZonedDateTime

/** Abstract representation of the service state */
trait State {
  /** Register new bleeding event */
  def addBleedingEvent(userId: Int, timestamp: ZonedDateTime): Unit

  /** Retrieve average cycle length for all the users */
  def getAverageCycleLength: Double
}
