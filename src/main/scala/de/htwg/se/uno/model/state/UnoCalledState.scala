package de.htwg.se.uno.model.state

case class UnoCalledState(context: UnoStates) extends GamePhase {
  override def playerSaysUno(): GamePhase = {
    val idx = context.gameState.currentPlayerIndex
    context.gameState = context.gameState.playerSaysUno(idx)
    context.setState(PlayerTurnState(context))
    context.state
  }
  override def playCard(): GamePhase = this
  override def drawCard(): GamePhase = this
  override def nextPlayer(): GamePhase = this
  override def dealInitialCards(): GamePhase = this
  override def checkForWinner(): GamePhase = this
  override def isValidPlay: Boolean = false
}
