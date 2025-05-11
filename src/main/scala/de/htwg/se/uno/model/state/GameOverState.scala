package de.htwg.se.uno.model.state

case class GameOverState(context: UnoStates) extends GamePhase {
  override def playCard(): GamePhase = this

  override def drawCard(): GamePhase = this

  override def nextPlayer(): GamePhase = this

  override def dealInitialCards(): GamePhase = this

  override def checkForWinner(): GamePhase = this

  override def playerSaysUno(): GamePhase = this

  override def isValidPlay: Boolean = false

  override def name: String = "GameOverState"
}
