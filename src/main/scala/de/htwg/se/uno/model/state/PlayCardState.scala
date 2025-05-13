package de.htwg.se.uno.model.state

import de.htwg.se.uno.model.*

case class PlayCardState(context: UnoStates, card: Card) extends GamePhase {
  override def playCard(): GamePhase = {
    context.gameState = context.gameState.playCard(card)
    context.setState(CheckWinnerState(context))
    context.state
  }
  override def drawCard(): GamePhase = this
  override def nextPlayer(): GamePhase = PlayerTurnState(context)
  override def dealInitialCards(): GamePhase = this
  override def checkForWinner(): GamePhase = GameOverState(context)
  override def playerSaysUno(): GamePhase = UnoCalledState(context)
  override def isValidPlay: Boolean = {
    val topCard = context.gameState.discardPile.lastOption
    context.gameState.isValidPlay(card, topCard)
  }
  //override def name: String = "PlayCardState"
}
