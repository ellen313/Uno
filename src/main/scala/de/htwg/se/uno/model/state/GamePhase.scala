package de.htwg.se.uno.model.state

trait GamePhase {
  def nextPlayer(): GamePhase
  def dealInitialCards(): GamePhase
  def checkForWinner(): GamePhase
  def playerSaysUno(): GamePhase
  def drawCard(): GamePhase
  def playCard(): GamePhase
  def isValidPlay: Boolean
  def name: String
}
