package de.htwg.se.uno.model.gameComponent.base.state

trait GamePhase {
  def nextPlayer(): GamePhase
  def dealInitialCards(): GamePhase
  def checkForWinner(): GamePhase
  def playerSaysUno(): GamePhase
  def drawCard(): GamePhase
  def playCard(): GamePhase
  def isValidPlay: Boolean
}
