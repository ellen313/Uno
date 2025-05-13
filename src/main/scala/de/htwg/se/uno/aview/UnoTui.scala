package de.htwg.se.uno.aview

import de.htwg.se.uno.model.*
import de.htwg.se.uno.util.Observer
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.controller.command.{DrawCardCommand, PlayCardCommand, UnoCalledCommand, ColorWishCommand}
import de.htwg.se.uno.aview.ColorPrinter.*

import scala.io.StdIn.readLine

class UnoTui(controller: GameBoard) extends Observer {

  var gameShouldExit = false
  var selectedColor: Option[String] = None

  controller.addObserver(this)

  def display(): Unit = {
    val state = controller.state

    if (state.players.isEmpty || gameShouldExit) return

    val currentPlayer = state.players(state.currentPlayerIndex)
    val topCard = state.discardPile.lastOption.getOrElse(return)

    println("\n--------------------------------------------------------------------")
    println(s"Player ${state.currentPlayerIndex + 1}'s turn!")

    // UNO Call Anzeige
    state.players.zipWithIndex
      .filter(_._1.hasSaidUno)
      .foreach { case (_, idx) =>
        if (idx == state.currentPlayerIndex) println("You said 'UNO'!")
        else println(s"Player ${idx + 1} said UNO")
      }

    print("Top Card: ")
    printCard(topCard)
    selectedColor.foreach(c => println(s"The color that was chosen: $c"))

    showHand(currentPlayer)

    val canPlay = currentPlayer.cards.exists(card => controller.isValidPlay(card, topCard, selectedColor))
    if (!canPlay) {
      println("No playable Card! You have to draw a card...")
      controller.executeCommand(DrawCardCommand(controller))
      controller.executeCommand(PlayCardCommand(-1)) // Signalisiert: Kein Spielzug
      return
    }

    println("Select a card (index) to play or type 'draw' to draw a card:")
    val input = readLine().trim
    handleInput(input)
  }

  def handleInput(input: String): Unit = {
    val state = controller.state
    val currentPlayer = state.players(state.currentPlayerIndex)

    input match {
      case "draw" =>
        val chosenCard = controller.gameState.players(controller.gameState.currentPlayerIndex).cards(cardIndex)
        controller.executeCommand(DrawCardCommand(controller))
        controller.executeCommand(PlayCardCommand(controller, chosenCard)) // Kein Spielzug
        return

      case "uno" =>
        controller.executeCommand(UnoCalledCommand(controller, context))
        return

      case _ =>
        try {
          val index = input.toInt
          if (index < 0 || index >= currentPlayer.cards.length) {
            println("Invalid index.")
            return
          }

          val chosenCard = currentPlayer.cards(index)
          if (chosenCard.isInstanceOf[WildCard]) {
            val color = chooseWildColor()
            controller.executeCommand(ColorWishCommand(unoState))
          }

          controller.executeCommand(PlayCardCommand(index))

          // Wenn Spieler auf eine Karte reduziert wurde und noch kein "UNO" gerufen:
          val updatedPlayer = controller.state.players(controller.state.currentPlayerIndex)
          if (updatedPlayer.cards.length == 1 && !updatedPlayer.hasSaidUno) {
            controller.executeCommand(UnoCalledCommand(controller.state.currentPlayerIndex))
            println("You said 'UNO'!")
          }

        } catch {
          case _: NumberFormatException =>
            println("Invalid input. Use card index or 'draw'.")
        }
    }

    checkForWinner()
  }

  def chooseWildColor(): String = {
    val colors = List("red", "green", "blue", "yellow")
    var chosen: Option[String] = None

    while (chosen.isEmpty) {
      println("Choose a color for the Wild Card:")
      colors.zipWithIndex.foreach { case (c, i) => println(s"$i - $c") }
      val input = readLine().trim
      input.toIntOption match {
        case Some(i) if i >= 0 && i < colors.length =>
          chosen = Some(colors(i))
        case _ => println("Invalid color. Try again.")
      }
    }

    chosen.get
  }

  def showHand(player: PlayerHand): Unit = {
    println("Your cards:")
    player.cards.zipWithIndex.foreach { case (card, i) =>
      print(s"$i - ")
      printCard(card)
    }
  }

  def checkForWinner(): Unit = {
    controller.checkForWinner() match {
      case Some(idx) =>
        println(s"Player ${idx + 1} wins! Game over.")
        gameShouldExit = true
      case None => // continue
    }
  }

  override def update(): Unit = if (!gameShouldExit) display()
}
