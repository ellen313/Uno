import model.*
import scala.io.StdIn.readLine

object UnoTui {
  def startGame(gameState: GameState): Unit = {
    var game = gameState
    var gameRunning = true

    while (gameRunning) {
      val currentPlayer = game.players(game.currentPlayerIndex)
      val topCard = game.gameBoard.discardPile.last

      println("\n----------------------------------")
      println(s"It's Player ${game.currentPlayerIndex + 1}'s turn!")
      println(s"Top Card: $topCard")
      println("Your Cards:")
      currentPlayer.cards.zipWithIndex.foreach { case (card, index) =>
        println(s"$index - $card")
      }

      if (!currentPlayer.cards.exists(card => game.gameBoard.isValidPlay(card, Some(topCard)))) {
        println("No playable Card! You have to draw a Card ...")
        val (drawnCard, updatedHand, updatedBoard) = game.gameBoard.drawCard(currentPlayer)
        game = game.copy(
          players = game.players.updated(game.currentPlayerIndex, updatedHand),
          gameBoard = updatedBoard
        )
      } else {
        println("Select a card (index) or type 'draw' to draw a card:")
        val input = readLine()

        if (input == "draw") {
          val (drawnCard, updatedHand, updatedBoard) = game.gameBoard.drawCard(currentPlayer)
          game = game.copy(
            players = game.players.updated(game.currentPlayerIndex, updatedHand),
            gameBoard = updatedBoard
          )
        } else {
          try {
            val cardIndex = input.toInt
            val chosenCard = currentPlayer.cards(cardIndex)

            if (game.gameBoard.isValidPlay(chosenCard, Some(topCard))) {
              game = game.gameBoard.playCard(chosenCard, game)
            } else {
              println("Invalid card! Please select a valid card.")
            }
          } catch {
            case _: NumberFormatException =>
              println("Invalid input! Please select a valid index or type 'draw'.")
          }
        }
      }
      
      game.checkForWinner() match {
        case Some(winnerIndex) =>
          println(s"Player ${winnerIndex + 1} has won! Game over.")
          gameRunning = false
        case None =>
          game = game.nextPlayer(game)
      }
    }
  }
}
