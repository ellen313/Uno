package de.htwg.se.uno.model.state

case class ColorWishState(context: UnoStates) extends GamePhase {
  override def playCard(): GamePhase = {
    // Integriere Farbwahl z.B. durch Setzen eines aktuellen Farbwunschs
    // Dummylogik: context.gameState = context.gameState.setCurrentColor(chosenColor)
    context.setState(PlayerTurnState(context))
    context.state
  }
  override def drawCard(): GamePhase = this
  override def nextPlayer(): GamePhase = this
  override def dealInitialCards(): GamePhase = this
  override def checkForWinner(): GamePhase = this
  override def playerSaysUno(): GamePhase = this
  override def isValidPlay: Boolean = false
}
