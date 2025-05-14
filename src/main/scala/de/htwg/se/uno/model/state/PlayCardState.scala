package de.htwg.se.uno.model.state

import de.htwg.se.uno.model.*

case class PlayCardState(context: UnoStates, card: Card) extends GamePhase {

  override def playCard(): GamePhase = {
    context.gameState = context.gameState.playCard(card)
    context.setState(CheckWinnerState(context))
    context.state
  }

  override def nextPlayer(): GamePhase = PlayerTurnState(context)

  override def dealInitialCards(): GamePhase = {
    println("Cannot deal cards during PlayCardState")
    this
  }

  override def checkForWinner(): GamePhase = GameOverState(context)

  override def playerSaysUno(): GamePhase = UnoCalledState(context)

  override def drawCard(): GamePhase = {
    println("Cannot draw a card during PlayCardState")
    this
  }

  override def isValidPlay: Boolean = {
    val topCard = context.gameState.discardPile.lastOption
    context.gameState.isValidPlay(card, topCard)
  }
}
