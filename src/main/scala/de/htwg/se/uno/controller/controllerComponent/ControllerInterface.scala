package de.htwg.se.uno.controller.controllerComponent

import de.htwg.se.uno.model.cardComponent.Card
import de.htwg.se.uno.model.gameComponent.GameStateInterface
import de.htwg.se.uno.util.{Command, Observable}

import scala.util.Try

trait ControllerInterface extends Observable {
  def gameState: Try[GameStateInterface]
  def startGame(gameBoard: ControllerInterface, players: Int, cardsPerPlayer: Int): Unit
  def updateState(newState: GameStateInterface): Unit
  def undoCommand(): Unit
  def redoCommand(): Unit
  def executeCommand(cmd: Command): Unit
  def checkForWinner(): Option[Int]
  val fullDeck: List[Card]
  def initGame(state: GameStateInterface): Unit
  def createDeckWithAllCards(): List[Card]
  def isValidPlay(card: Card, topCard: Card, selectedColor: Option[String]): Boolean
}
