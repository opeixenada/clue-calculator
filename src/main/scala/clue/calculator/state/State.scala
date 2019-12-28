package clue.calculator.state

import clue.calculator.models.Event

/** Abstract representation of the service state */
trait State {
  /** Register new symptom event */
  def addEvent(event: Event): Unit

  /** Retrieve average cycle length for all the users */
  def getAverageCycleLength: Double
}
