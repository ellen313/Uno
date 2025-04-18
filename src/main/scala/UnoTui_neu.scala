import model._
import ColorPrinter._
import scala.io.StdIn.readLine

class UnoTui_neu(var game: GameState) {

  var selectedColor: Option[String] = None

  def display(): Unit = {
    val currentPlayer = game.players(game.currentPlayerIndex)
    val topCard = game.gameBoard.discardPile.last

    println("\n--------------------------------------------------------------------")
    println(s"Player ${game.currentPlayerIndex + 1}'s turn!")
    println(s"Top Card: ")
    printCard(topCard)

    if (selectedColor.isDefined) {
      println(s"Selected Color: ${selectedColor.get}")
    }

    showHand(currentPlayer)

    val playable = currentPlayer.cards.exists(card =>
      game.gameBoard.isValidPlay(card, Some(topCard), selectedColor)
    )

    if (!playable) {
      println("No playable cards. Drawing a card...")
      drawCardForPlayer()
    } else {
      println("Choose a card index or type 'draw':")
    }
  }

  def handleCardSelection(input: String): Unit = {
    val currentPlayer = game.players(game.currentPlayerIndex)

    input match {
      case "draw" =>
        drawCardForPlayer()
      case _ =>
        try {
          val index = input.toInt
          if (index <= 0 && index < currentPlayer.cards.length) {
            val selectedCard = currentPlayer.cards(index)

            // Wildcard Handling
            if (selectedCard.isInstanceOf[WildCard]) {
              selectedColor = askForColor()
            }

            //Valid Play Check
            val topCard = game.gameBoard.discardPile.last
            if (game.gameBoard.isValidPlay(selectedCard, Some(topCard), selectedColor)) {
              //selected card is valid
              println(s"Played: $selectedCard")
              game = game.gameBoard.playCard(selectedCard, game)
              if (!selectedCard.isInstanceOf[WildCard]) selectedColor = None

              if (!currentPlayer.hasSaidUno && currentPlayer.cards.length == 2) {
                println("You said UNO!")
                game = game.playerSaysUno(game.currentPlayerIndex)
              }

              checkWinner()
            } else {
              //selected card is invalid
              println("Invalid play. Try again.")
              display()
            }
          } else {
            println("invalid card index.")
            display()
          }
        } catch {
          case _: NumberFormatException =>
            println("Please enter a valid number or 'draw'.")
            display()
        }
    }
  }

  def drawCardForPlayer(): Unit = {
    val currentPlayer = game.players(game.currentPlayerIndex)
    val (drawnCard, updatedHand, updatedBoard) = game.gameBoard.drawCard(currentPlayer)

    println(s"You drew: $drawnCard")

    game = game.copy(
      players = game.players.updated(game.currentPlayerIndex, updatedHand),
      gameBoard = updatedBoard
    )

    nextTurn()
  }

  def showHand(player: PlayerHand): Unit = {
    println("Your hand:")
    player.cards.zipWithIndex.foreach { case (card, index) =>
      print(s"$index - ")
      printCard(card)
    }
  }

  def askForColor(): Option[String] = {
    val colors = List("red", "green", "blue", "yellow")
    println("Choose a color for the WildCard:")

    colors.zipWithIndex.foreach { case (color, i) =>
      println(s"$i - $color")
    }

    var valid = false
    var chosen: Option[String] = None

    while (!valid) {
      val input = readLine("Color index: ").trim
      try {
        val i = input.toInt
        if (i >= 0 && i < colors.length) {
          chosen = Some(colors(i))
          valid = true
        } else println("Invalid number.")
      } catch {
        case _: NumberFormatException => println("Please enter a valid number.")
      }
    }
    chosen
  }

  def nextTurn(): Unit = {
    game = game.copy(currentPlayerIndex = (game.currentPlayerIndex + 1) % game.players.length)
    display()
  }

  def checkWinner(): Unit = {
    game.checkForWinner() match {
      case Some(index) if game.players(index).cards.isEmpty =>
        println(s"Player ${index + 1} wins! Game Over.")
      case _ =>
        nextTurn()
    }
  }
}