package de.htwg.se.uno.controller.controllerComponent

import de.htwg.se.uno.model.cardComponent.Card
import de.htwg.se.uno.model.gameComponent.base.GameState
import de.htwg.se.uno.util.{Command, Observable}

import scala.util.Try

trait ControllerInterface extends Observable {
  def gameState: Try[GameState]
  def startGame(players: Int, cardsPerPlayer: Int): Unit
  def updateState(newState: GameState): Unit
  def undoCommand(): Unit
  def redoCommand(): Unit
  def executeCommand(cmd: Command): Unit
  def checkForWinner(): Option[Int]
  val fullDeck: List[Card]
  def initGame(state: GameState): Unit
  def isValidPlay(card: Card, topCard: Card, selectedColor: Option[String]): Boolean
}
