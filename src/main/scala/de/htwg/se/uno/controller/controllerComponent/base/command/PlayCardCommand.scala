package de.htwg.se.uno.controller.controllerComponent.base.command

import de.htwg.se.uno.controller.controllerComponent.base.GameBoard
import de.htwg.se.uno.model.*
import de.htwg.se.uno.model.cardComponent.{ActionCard, Card, WildCard}
import de.htwg.se.uno.model.gameComponent.base.GameState
import de.htwg.se.uno.util.Command

case class PlayCardCommand(card: Card, chooseColor: Option[String] = None) extends Command {
  private var validPlay: Boolean = false
  private var previousState: Option[GameState] = None

  override def execute(): Unit = {
    GameBoard.gameState.foreach { state =>
      previousState = Some(state)
      val color = chooseColor.getOrElse("")

      if (state.isValidPlay(card, state.discardPile.headOption, chooseColor.orElse(state.selectedColor))) {
        validPlay = true

        val newState = state.playCard(card)

        val transitionedState = card match {
          case ActionCard(_, "skip") =>
            newState.nextPlayer().nextPlayer()

          case ActionCard(_, "reverse") =>
            newState.copy(isReversed = !newState.isReversed).nextPlayer()

          case ActionCard(_, "draw two") =>
            newState.handleDrawCards(2).nextPlayer().nextPlayer()

          case WildCard("wild draw four") =>
            newState.copy(selectedColor = chooseColor).handleDrawCards(4).nextPlayer()

          case WildCard(_) =>
            newState.copy(selectedColor = chooseColor).nextPlayer()

          case _ =>
            newState.nextPlayer()
        }

        // debugging
        //      println(s"Before update: currentPlayerIndex = ${newState.currentPlayerIndex}")
        //      println(s"After transition: currentPlayerIndex = ${transitionedState.currentPlayerIndex}")

        GameBoard.updateState(transitionedState)
        transitionedState.notifyObservers()
      } else {
        validPlay = false
        println("Invalid play. Try again")
      }
    }
  }

  override def undo(): Unit = {
    previousState.foreach { oldState =>
      GameBoard.updateState(oldState)
    }
  }

  override def redo(): Unit = execute()
}
