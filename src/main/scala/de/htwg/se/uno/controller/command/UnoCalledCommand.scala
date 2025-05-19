package de.htwg.se.uno.controller.command

import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.model.state.{GameOverState, UnoStates}

case class UnoCalledCommand(context: UnoStates) extends Command {
  override def execute(): Unit = {
    val idx = GameBoard.gameState.currentPlayerIndex
    val updatedGame = GameBoard.gameState.playerSaysUno(idx)
    GameBoard.updateState(updatedGame)

    val player = updatedGame.players(idx)
    if (player.cards.isEmpty && player.hasSaidUno) {
      context.setState(GameOverState(context))
    }
  }
}
