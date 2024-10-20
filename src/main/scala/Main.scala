import scala.util.Random

object Main {
  def main(args: Array[String]): Unit = {
    val numberPlayers =  2
    val cardsPerPlayer =  7

    //intialize empty: Gamenboard, PlayerHands, GameState
    val initialGameBoard = GameBoard(List.empty[Card], List.empty[Card]).shuffleDeck()
    val playerHands = List.fill(numberPlayers)(PlayerHand(List.empty[Card]))
    var gameState = GameState(playerHands, initialGameBoard, 0, initialGameBoard.drawPile)

    //start game by dealing cards to players
    def dealInitialCards(gameState: GameState, cardsPerPlayer: Int): GameState = {
      var updatedGameState = gameState
      for(_ <- 1 to cardsPerPlayer) {
        for (playerIndex <- updatedGameState.players.indices) {
          val (drawnCard, updatedHand, updatedBoard) = updatedGameState.gameBoard.drawCard(updatedGameState.players(playerIndex))
          updatedGameState = updatedGameState.copy(players = updatedGameState.players.updated(playerIndex, updatedHand), gameBoard = updatedBoard)
        }
      }
      updatedGameState
    }
    gameState = dealInitialCards(gameState, cardsPerPlayer)

    for(i <- gameState.players.indices) {
      println(s"player ${i + 1}'s hand:")
      gameState.players(i).displayHand()
      print("\n")
    }

  }
}
