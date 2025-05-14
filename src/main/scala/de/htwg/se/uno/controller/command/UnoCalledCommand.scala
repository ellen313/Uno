package de.htwg.se.uno.controller.command

import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.model.state.{GameOverState, UnoStates}

case class UnoCalledCommand(gameBoard: GameBoard, context: UnoStates) extends Command {
  override def execute(): Unit = {
    val idx = gameBoard.gameState.currentPlayerIndex
    val updatedGame = gameBoard.gameState.playerSaysUno(idx)
    gameBoard.gameState = updatedGame

    val player = updatedGame.players(idx)
    if (player.cards.isEmpty && player.hasSaidUno) {
      context.setState(GameOverState(context))
    }
  }
}
