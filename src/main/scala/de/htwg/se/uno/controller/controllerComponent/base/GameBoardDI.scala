package de.htwg.se.uno.controller.controllerComponent.base

import com.google.inject.Inject
import de.htwg.se.uno.controller.controllerComponent.ControllerInterface
import de.htwg.se.uno.model.gameComponent.base.GameState
import de.htwg.se.uno.util.Command

import scala.util.Try

class GameBoardDI @Inject() extends ControllerInterface {

  def initGame(state: GameState): Unit = GameBoard.initGame(state)

  def gameState: Try[GameState] = GameBoard.gameState

  override def startGame(players: Int, cardsPerPlayer: Int): Unit = GameBoard.startGame(players, cardsPerPlayer)

  override def updateState(newState: GameState): Unit = GameBoard.updateState(newState)

  override def undoCommand(): Unit = GameBoard.undoCommand()

  override def redoCommand(): Unit = GameBoard.redoCommand()

  override def executeCommand(cmd: Command): Unit = GameBoard.executeCommand(cmd)

  override def checkForWinner(): Option[Int] = GameBoard.checkForWinner()
}
