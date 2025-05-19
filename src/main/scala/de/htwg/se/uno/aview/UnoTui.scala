package de.htwg.se.uno.aview

import de.htwg.se.uno.model.*
import de.htwg.se.uno.util.Observer
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.controller.command.*
import de.htwg.se.uno.aview.ColorPrinter.*
import de.htwg.se.uno.model.state.UnoStates

import scala.io.StdIn.readLine

class UnoTui(context: UnoStates) extends Observer {

  private var gameShouldExit = false
  var selectedColor: Option[String] = None

  GameBoard.addObserver(this)

  def display(): Unit = {

    val state = GameBoard.gameState
    if (state.players.isEmpty || gameShouldExit) return

    val currentPlayer = state.players(state.currentPlayerIndex)
    val topCard = state.discardPile.headOption.getOrElse(return)

    println("\n--------------------------------------------------------------------")
    println(s"Player ${state.currentPlayerIndex + 1}'s turn!")

    val unoPlayers = state.players.zipWithIndex.filter(_._1.hasSaidUno)
    unoPlayers.foreach { case (_, idx) =>
        if (idx == state.currentPlayerIndex) println("You said 'UNO'!")
        else println(s"Player ${idx + 1} said UNO")
      }

    print("Top Card: ")
    printCard(topCard)
    selectedColor.foreach(c => println(s"The color that was chosen: $c"))

    showHand(currentPlayer)

    if (!currentPlayer.cards.exists(card => state.isValidPlay(card, Some(topCard), selectedColor))) {
      println("No playable Card! You have to draw a card...")
      GameBoard.executeCommand(DrawCardCommand())
      gameShouldExit = false
      display()
    } else {
      println("Select a card (index) to play or type 'draw' to draw a card:")
    }
  }

  def handleInput(input: String): Unit = {
    val state = GameBoard.gameState
    val currentPlayer = state.players(state.currentPlayerIndex)

    input match {
      case "draw" =>
        val (newState, drawnCard) = state.drawCardAndReturnDrawn()
        println(s"You drew: $drawnCard")

        if (newState.isValidPlay(drawnCard, newState.discardPile.headOption, newState.selectedColor)) {
          println("Playing drawn card...")
          GameBoard.updateState(newState)

          val chosenColor =
            if (drawnCard.isInstanceOf[WildCard]) Some(chooseWildColor())
            else None

          GameBoard.executeCommand(PlayCardCommand(drawnCard, chosenColor))
        } else {
          println("Card cannot be played, turn ends.")
          val skipped = newState.nextPlayer()
          GameBoard.updateState(skipped)
          skipped.notifyObservers()
        }

      case _ =>
        try {
          val index = input.toInt
          if (index < 0 || index >= currentPlayer.cards.length) {
            println("Invalid index.")
          } else {
            val chosenCard = currentPlayer.cards(index)

            val chosenColor =
              if (chosenCard.isInstanceOf[WildCard]) Some(chooseWildColor())
              else None

            GameBoard.executeCommand(PlayCardCommand(chosenCard, chosenColor))
          }
        } catch {
          case _: NumberFormatException =>
            println("Invalid input. Use card index or 'draw'.")
        }
    }

    checkUno()
    checkForWinner()
  }

  def chooseWildColor(inputFunc: () => String = () => readLine()): String = {
    val colors = List("red", "green", "blue", "yellow")
    var validColor = false
    var chosenColor = ""

    while (!validColor) {
      println("Please choose a color for the Wild Card:")
      colors.zipWithIndex.foreach { case (c, i) => println(s"$i - $c") }

      inputFunc().trim match {
        case input if input.matches("[0-3]") =>
          chosenColor = colors(input.toInt)
          println(s"Wild Card color changed to: $chosenColor")
          validColor = true
        case _ => println("Invalid input. Please enter a number between 0 and 3.")
      }
    }

    chosenColor
  }

  private def showHand(player: PlayerHand): Unit = {
    println("Your cards:")
    player.cards.zipWithIndex.foreach { case (card, i) =>
      print(s"$i - ")
      printCard(card)
    }
  }

  def checkForWinner(): Unit = {
    GameBoard.checkForWinner() match {
      case Some(idx) =>
        println(s"Player ${idx + 1} wins! Game over.")
        gameShouldExit = true
      case None => ()
    }
  }

  def shouldExit: Boolean = gameShouldExit

  def setShouldExit(value: Boolean): Unit = {
    gameShouldExit = value
  }

  private def checkUno(): Unit = {
    val state = GameBoard.gameState
    val updatedPlayer = state.players(state.currentPlayerIndex)

    if (updatedPlayer.cards.length == 1 && !updatedPlayer.hasSaidUno) {
      GameBoard.executeCommand(UnoCalledCommand(context))
      println("You said 'UNO'!")
    }
  }

  override def update(): Unit = if (!gameShouldExit) display()
}
