package de.htwg.se.uno.model.state

case class SkipState(context: UnoStates) extends GamePhase {
  override def nextPlayer(): GamePhase = {
    context.gameState = context.gameState.nextPlayer().nextPlayer()
    context.setState(PlayerTurnState(context))
    context.state
  }
  override def playCard(): GamePhase = this
  override def drawCard(): GamePhase = this
  override def dealInitialCards(): GamePhase = this
  override def checkForWinner(): GamePhase = this
  override def playerSaysUno(): GamePhase = this
  override def isValidPlay: Boolean = false
}
