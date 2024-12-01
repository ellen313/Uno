object Main {
  def main(args: Array[String]): Unit = {
    val numberPlayers =  2
    val cardsPerPlayer =  7

    //intialize empty: Gamenboard, PlayerHands, GameState
    val initialGameBoard = GameBoard(List.empty[Card], List.empty[Card]).shuffleDeck()

    val playerHands = List.fill(numberPlayers)(PlayerHand(List.empty[Card]))
    var gameState = GameState(playerHands, initialGameBoard, 0, initialGameBoard.drawPile)

    //start game by dealing cards to players
    gameState = gameState.dealInitialCards(cardsPerPlayer)

    for(i <- gameState.players.indices) {
      println(s"player ${i + 1}'s hand:")
      gameState.players(i).displayHand()
      print("\n")
    }
  }
}
