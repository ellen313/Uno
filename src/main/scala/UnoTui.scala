import model._
import scala.io.StdIn.readLine

object UnoTui {

  def startGame(gameState: GameState): Unit = {
    var game = gameState
    var gameRunning = true
    var selectedColor: Option[String] = None

    while (gameRunning) {
      val currentPlayer = game.players(game.currentPlayerIndex)
      val topCard = game.gameBoard.discardPile.last

      println("\n--------------------------------------------------------------------")
      println(s"Player ${game.currentPlayerIndex + 1}'s turn!")

      val playersWhoSaidUno = game.players.zipWithIndex
        .filter { case (player, _) => player.hasSaidUno }
        .map { case (_, index) => s"Player ${index + 1}" }
        .mkString(", ")
      
      if (playersWhoSaidUno.nonEmpty) {
        println(s"$playersWhoSaidUno said UNO")
      }

      println(s"Top Card: $topCard")
      if (selectedColor.isDefined) {
        println(s"The color that was chosen: ${selectedColor.get}")
      }
      showHand(currentPlayer)

      if (!currentPlayer.cards.exists(card => game.gameBoard.isValidPlay(card, Some(topCard)))) {
        println("No playable Card! You have to draw a Card...")
        game = drawCardForPlayer(game, currentPlayer)
      } else {

        var validInput = false
        var firstAttempt = true

        while (!validInput) {
          if (firstAttempt) {
            println("Select a card (index) to play or type 'draw' to draw a card:")
            firstAttempt = false
          }

          val input = readLine().trim

          input match {
            case "draw" =>
              game = drawCardForPlayer(game, currentPlayer)
              validInput = true

            case _ =>
              try {
                val cardIndex = input.toInt
                if (cardIndex >= 0 && cardIndex < currentPlayer.cards.length) {
                  val chosenCard = currentPlayer.cards(cardIndex)

                  if (chosenCard.isInstanceOf[WildCard]) {
                    val colors = List("Red", "Green", "Blue", "Yellow")
                    var validColor = false

                    while (!validColor) {
                      println("Please choose a color for the Wild Card:")
                      colors.zipWithIndex.foreach { case (color, index) =>
                        println(s"$index - $color")
                      }

                      val colorInput = readLine().trim
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
                  if (selectedColor.isDefined && chosenCard.color.toLowerCase != selectedColor.get.toLowerCase && !chosenCard.isInstanceOf[WildCard]) {
                    println(s"Invalid play! The color must be ${selectedColor.get}. Try again.")
                  } else {
                    println(s"Played: $chosenCard")
                    game = game.gameBoard.playCard(chosenCard, game)
                    if (!chosenCard.isInstanceOf[WildCard]) {
                      selectedColor = None
                    }
                    
                    validInput = true
                    if (!currentPlayer.hasSaidUno && currentPlayer.cards.length == 2) {
                      println("You said 'UNO'!")
                      game = game.playerSaysUno(game.currentPlayerIndex)
                    }
                  }
                } else {
                  println("Invalid index! Please select a valid card.")
                }
              } catch {
                case _: NumberFormatException =>
                  println("Invalid input! Please select a valid index or type 'draw':")
              }
          }
        }
      }
      //check for winner
      game.checkForWinner() match {
        case Some(winnerIndex) =>
          if (game.players(winnerIndex).cards.isEmpty) {
            println(s"Player ${winnerIndex + 1} wins! Game over.")
            gameRunning = false
          }
        case None =>
      }
    }
  }

  //show current player hand
  def showHand(playerHand: PlayerHand): Unit = {
    println("Your Cards:")
    playerHand.cards.zipWithIndex.foreach { case (card, index) =>
      println(s"$index - $card")
    }
  }

  //drawing a card
  def drawCardForPlayer(game: GameState, currentPlayer: PlayerHand): GameState = {
    val (drawnCard, updatedHand, updatedBoard) = game.gameBoard.drawCard(currentPlayer)
    println(s"You drew: $drawnCard")
    game.copy(
      players = game.players.updated(game.currentPlayerIndex, updatedHand),
      gameBoard = updatedBoard
    )
  }

  //selecting a card to play
  def handleCardSelection(input: String, game: GameState, currentPlayer: PlayerHand, topCard: Card): GameState = {
    try {
      val cardIndex = input.toInt
      
      if (cardIndex >= 0 && cardIndex < currentPlayer.cards.length) {
        val chosenCard = currentPlayer.cards(cardIndex)

        if (game.gameBoard.isValidPlay(chosenCard, Some(topCard))) {
          //selected card is valid
          println(s"Played: $chosenCard")
          
          val updatedGameState = game.gameBoard.playCard(chosenCard, game)
          updatedGameState
        } else {
          //selected card is invalid
          println("Invalid card! Please select a valid card.")
          game
        }
      } else {
        //card index out of bounds
        println("Invalid input! Please select a valid index or type 'draw'.")
        game
      }
    } catch {
      case _: NumberFormatException =>
        //input is not a number
        println("Invalid input! Please select a valid index or type 'draw'.")
        game
    }
  }
}
