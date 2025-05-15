package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard

case class PlayCardCommand(card: Card) extends Command {
  private var validPlay: Boolean = false

  private def chooseWildColor(): String = {
    val colors = List("red", "green", "blue", "yellow")
    var color: Option[String] = None

    while (color.isEmpty) {
      println("Choose a color: 0 - red, 1 - green, 2 - blue, 3 - yellow")
      val input = scala.io.StdIn.readLine()
      input match {
        case "0" => color = Some("red")
        case "1" => color = Some("green")
        case "2" => color = Some("blue")
        case "3" => color = Some("yellow")
        case _ => println("Invalid choice. Try again.")
      }
    }
    color.get
  }

  override def execute(): Unit = {
    val state = GameBoard.gameState

    if (state.isValidPlay(card, state.discardPile.headOption, state.selectedColor)) {
      validPlay = true

      val chosenColor = card match {
        case WildCard(_) | WildCard("wild draw four") =>
          chooseWildColor()
        case _ => state.selectedColor.getOrElse("")
      }

      val newState = state.playCard(card, Some(chosenColor))

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

      println(s"Before update: currentPlayerIndex = ${newState.currentPlayerIndex}")
      println(s"After transition: currentPlayerIndex = ${transitionedState.currentPlayerIndex}")

      GameBoard.updateState(transitionedState) // HIER wird der neue currentPlayerIndex gesetzt
      transitionedState.notifyObservers()
    } else {
      validPlay = false
      println("Invalid play. Try again")
    }
  }
}
