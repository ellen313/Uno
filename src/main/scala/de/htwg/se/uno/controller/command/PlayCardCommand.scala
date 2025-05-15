package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard

case class PlayCardCommand(card: Card) extends Command {
  private var validPlay: Boolean = false

  override def execute(): Unit = {
    val state = GameBoard.gameState

    if (state.isValidPlay(card, state.discardPile.headOption, state.selectedColor)) {
      validPlay = true
      val newState = state.playCard(card)

      val transitionedState = card match {
        case ActionCard(_, "skip") =>
          newState.nextPlayer().nextPlayer()

        case ActionCard(_, "reverse") =>
          newState.copy(isReversed = !newState.isReversed).nextPlayer()

        case ActionCard(_, "draw two") =>
          newState.handleDrawCards(2).nextPlayer()

        case WildCard("wild draw four") =>
          newState.handleDrawCards(4).nextPlayer()

        case _ =>
          newState.nextPlayer()
      }

      GameBoard.updateState(transitionedState)
      transitionedState.notifyObservers()
    } else {
      println("Invalid play. Try again")
    }
  }
}
