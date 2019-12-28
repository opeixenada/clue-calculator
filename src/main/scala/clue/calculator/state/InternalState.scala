package clue.calculator.state

case class InternalState(usersState: Map[Int, UserState] = Map.empty.withDefaultValue(UserState()),
                         cyclesCountAllUsers: Int = 0,
                         cyclesLengthSumAllUsers: Long = 0) {
  def update(userId: Int, userState: UserState, newCycleLength: Option[Long] = None): InternalState = {
    val newUsersState = usersState + (userId -> userState)
    val newCyclesCountAllUsers = cyclesCountAllUsers + newCycleLength.map(_ => 1).getOrElse(0)
    val newCyclesLengthSumAllUsers = cyclesLengthSumAllUsers + newCycleLength.getOrElse(0.toLong)
    InternalState(newUsersState, newCyclesCountAllUsers, newCyclesLengthSumAllUsers)
  }

  def getAverageCycleLength: Double = {
    if (cyclesCountAllUsers > 0) cyclesLengthSumAllUsers.toDouble / cyclesCountAllUsers else 0
  }
}