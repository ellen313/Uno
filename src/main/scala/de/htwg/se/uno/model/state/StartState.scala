package de.htwg.se.uno.model.state

import de.htwg.se.uno.model.*

case class StartState(context: UnoStates) extends GamePhase {
  override def playCard(): GamePhase = this
  override def drawCard(): GamePhase = this
  override def nextPlayer(): GamePhase = this
  override def dealInitialCards(): GamePhase = {
    context.gameState = context.gameState.dealInitialCards(7)
    context.setState(PlayerTurnState(context))
    context.state
  }
  override def checkForWinner(): GamePhase = this
  override def playerSaysUno(): GamePhase = this
  override def isValidPlay: Boolean = false
  //override def name: String = "StartState"
}
