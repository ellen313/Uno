package de.htwg.se.uno.model.state

case class CheckWinnerState(context: UnoStates) extends GamePhase {
  override def checkForWinner(): GamePhase = {
    context.gameState.checkForWinner() match {
      case Some(_) =>
        context.setState(GameOverState(context))
      case None =>
        context.setState(PlayerTurnState(context))
    }
    context.state
  }
  override def playCard(): GamePhase = this
  override def drawCard(): GamePhase = this
  override def nextPlayer(): GamePhase = this
  override def dealInitialCards(): GamePhase = this
  override def playerSaysUno(): GamePhase = this
  override def isValidPlay: Boolean = false
}