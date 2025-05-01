package view

import model.*
import view.ColorPrinter.*

import scala.io.StdIn.readLine

class UnoTui(var game: GameState) extends Observer {

  var gameShouldExit = false
  var selectedColor: Option[String] = None

  game.addObserver(this)

  def display(): Unit = {

    if (game.players.isEmpty || gameShouldExit) return

    val currentPlayer = game.players(game.currentPlayerIndex)
    val topCard = game.discardPile.lastOption.getOrElse {
      print("Discard pile empty"); return
    }

    println("\n--------------------------------------------------------------------")
    println(s"Player ${game.currentPlayerIndex + 1}'s turn!")

    val playersWhoSaidUno = game.players.zipWithIndex
      .filter { case (player, _) => player.hasSaidUno }
      .map { case (_, index) => s"Player ${index + 1}" }
      .mkString(", ")

    if (playersWhoSaidUno.nonEmpty) {
      println(s"$playersWhoSaidUno said UNO")
    }

    print("Top Card: ")
    printCard(topCard)
    if (selectedColor.isDefined) {
      println(s"The color that was chosen: ${selectedColor.get}")
    }

    showHand(currentPlayer)

    if (!currentPlayer.cards.exists(card => game.isValidPlay(card, Some(topCard), selectedColor))) {
      println("No playable Card! You have to draw a Card...")
      game = drawCardForPlayer(currentPlayer)
      display()
    } else {
      println("Select a card (index) to play or type 'draw' to draw a card:")
    }
  }

  def chooseWildColor(inputFunc: () => String = () => readLine()): Unit = {
    val colors = List("red", "green", "blue", "yellow")
    var validColor = false
    while (!validColor) {
      println("Please choose a color for the Wild Card:")
      colors.zipWithIndex.foreach { case (color, index) =>
        println(s"$index - $color")
      }
      val colorInput = inputFunc().trim
      try {
        val colorIndex = colorInput.toInt
        if (colorIndex >= 0 && colorIndex < colors.length) {
          val chosenColor = colors(colorIndex)
          selectedColor = Some(chosenColor)
          println(s"Wild Card color changed to: $chosenColor")
          validColor = true
        } else {
          println("Invalid color choice. Please try again.")
        }
      } catch {
        case _: NumberFormatException =>
          println("Invalid input. Please enter a number between 0 and 3.")
      }
    }
  }

  def handleCardSelection(input: String): Unit = {

    if (gameShouldExit) return

    val currentPlayer = game.players(game.currentPlayerIndex)
    val topCard = game.discardPile.last

    input match {
      case "draw" =>
        game = drawCardForPlayer(currentPlayer)
        game = game.nextPlayer()
        println("Turn complete.")
        display()

      case _ =>
        try {
          val cardIndex = input.toInt
          if (cardIndex >= 0 && cardIndex < currentPlayer.cards.length) {
            val chosenCard = currentPlayer.cards(cardIndex)

            chosenCard match {
              case wild: WildCard =>
                chooseWildColor()
                println(s"Played: $wild")
                game = game.playCard(wild)

              case _ =>
                if (selectedColor.isDefined &&
                  chosenCard.color.toLowerCase != selectedColor.get.toLowerCase &&
                  !chosenCard.isInstanceOf[WildCard]) {
                  println(s"Invalid play! The color must be ${selectedColor.get}. Try again.")
                  display()
                  return
                }

                if (!game.isValidPlay(chosenCard, Some(topCard), selectedColor)) {
                  println("Invalid card! Please select a valid card.")
                  display()
                  return
                }

                println(s"Played: $chosenCard")
                game = game.playCard(chosenCard)
            }

            if (!chosenCard.isInstanceOf[WildCard]) selectedColor = None

            val updatedPlayer = game.players((game.currentPlayerIndex + game.players.length - 1) % game.players.length)

            if (!updatedPlayer.hasSaidUno && updatedPlayer.cards.length == 1) {
              println("You said 'UNO'!")
              this.game = game.playerSaysUno((game.currentPlayerIndex + game.players.length - 1) % game.players.length)
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

  private def showHand(playerHand: PlayerHand): Unit = {
    println("Your Cards:")
    playerHand.cards.zipWithIndex.foreach { case (card, index) =>
      print(s"$index - ")
      printCard(card)
    }
  }

  def drawCardForPlayer(currentPlayer: PlayerHand): GameState = {
    val (drawnCard, updatedHand, updatedDrawPile, updatedDiscardPile) = game.drawCard(currentPlayer, game.drawPile, game.discardPile)
    println(s"You drew: $drawnCard")
    game.copy(
      players = game.players.updated(game.currentPlayerIndex, updatedHand),
      drawPile = updatedDrawPile,
      discardPile = updatedDiscardPile
    )
  }

  def checkForWinner(): Unit = {
    game.checkForWinner() match {
      case Some(winnerIndex) =>
        //if game.players(winnerIndex).cards.isEmpty =>
          println(s"Player ${winnerIndex + 1} wins! Game over.")
          gameShouldExit = true
          game = game.copy(players = List.empty)
        case None =>
    }
  }

  def shouldExit: Boolean = gameShouldExit

  override def update(): Unit = {
    if (!gameShouldExit) display()
  }
}
