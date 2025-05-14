package de.htwg.se.uno.aview

import de.htwg.se.uno.model.*
import de.htwg.se.uno.util.Observer
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.controller.command.{DrawCardCommand, PlayCardCommand, UnoCalledCommand, ColorWishCommand}
import de.htwg.se.uno.aview.ColorPrinter.*

import scala.io.StdIn.readLine

class UnoTui(val controller: GameBoard) extends Observer {

  var gameShouldExit = false
  var selectedColor: Option[String] = None

  controller.addObserver(this)

  def display(): Unit = {

    if (controller.players.isEmpty || gameShouldExit) return

    val currentPlayer = controller.players(controller.currentPlayerIndex)
    val topCard = controller.discardPile.lastOption.getOrElse(return)

    println("\n--------------------------------------------------------------------")
    println(s"Player ${controller.currentPlayerIndex + 1}'s turn!")

    // UNO Call Anzeige
    val unoPlayers = controller.players.zipWithIndex.filter(_._1.hasSaidUno)
    unoPlayers.foreach { case (_, idx) =>
        if (idx == controller.currentPlayerIndex) println("You said 'UNO'!")
        else println(s"Player ${idx + 1} said UNO")
      }

    print("Top Card: ")
    printCard(topCard)
    selectedColor.foreach(c => println(s"The color that was chosen: $c"))

    showHand(currentPlayer)

    if (!currentPlayer.cards.exists(card => controller.isValidPlay(card, topCard, controller.selectedColor))) {
      println("No playable Card! You have to draw a card...")
      controller.executeCommand(DrawCardCommand(controller))
      gameShouldExit = false
      display()
    } else {
      println("Select a card (index) to play or type 'draw' to draw a card:")
    }
  }

  def handleInput(input: String): Unit = {
    if (gameShouldExit) return

    val currentPlayer = controller.players(controller.currentPlayerIndex)

    input match {
      case "draw" =>
        val drawCommand = DrawCardCommand(controller)
        controller.executeCommand(drawCommand)

        drawCommand.drawnCard match {
          case Some(card) => println(s"You drew: $card")
          case None => println("No card was drawn.")
        }
        display()


      case _ =>
        try {
          val index = input.toInt
          if (index >= 0 && index < currentPlayer.cards.length) {
            val chosenCard = currentPlayer.cards(index)

            if (chosenCard.isInstanceOf[WildCard]) {
              val color = chooseWildColor()
              controller.setSelectedColor(color)
            }

            controller.executeCommand(PlayCardCommand(controller, chosenCard))

            val updatedPlayer = controller.players(controller.currentPlayerIndex)
            if (updatedPlayer.cards.length == 1 && !updatedPlayer.hasSaidUno) {
              controller.executeCommand(UnoCalledCommand(controller, null))
              println("You said 'UNO'!")
            }

            checkForWinner()
            println("Turn complete.")
            display()
          } else {
            println("Invalid index! Please select a valid card.")
            display()
          }
        } catch {
        case _: NumberFormatException =>
          println("Invalid input! Please select a valid index or type 'draw':")
          display()
      }
    }
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
    controller.checkForWinner() match {
      case Some(idx) =>
        println(s"Player ${idx + 1} wins! Game over.")
        gameShouldExit = true
      case None => ()
    }
  }

  def shouldExit: Boolean = gameShouldExit

  override def update(): Unit = if (!gameShouldExit) display()
}
