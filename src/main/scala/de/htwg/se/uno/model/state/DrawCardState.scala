package de.htwg.se.uno.model.state

case class DrawCardState(context: UnoStates) extends GamePhase {
  override def playCard(): GamePhase = this
  override def drawCard(): GamePhase = {
    val currentPlayer = context.gameState.players(context.gameState.currentPlayerIndex)
    val (card, updatedHand, updatedDrawPile, updatedDiscardPile) =
      context.gameState.drawCard(currentPlayer, context.gameState.drawPile, context.gameState.discardPile)

    val updatedPlayers = context.gameState.players.updated(context.gameState.currentPlayerIndex, updatedHand)
    context.gameState = context.gameState.copy(
      players = updatedPlayers,
      drawPile = updatedDrawPile,
      discardPile = updatedDiscardPile
    )
    context.setState(PlayerTurnState(context))
    context.state
  }
  override def nextPlayer(): GamePhase = this
  override def dealInitialCards(): GamePhase = this
  override def checkForWinner(): GamePhase = this
  override def playerSaysUno(): GamePhase = this
  override def isValidPlay: Boolean = false
  //override def name: String = "DrawCardState"
}
