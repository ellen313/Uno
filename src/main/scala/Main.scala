import model.*

object Main {
  def main(args: Array[String]): Unit = {
    val numberPlayers =  2
    val cardsPerPlayer =  7

    //intialize empty: Gamenboard, PlayerHands, GameState
    val initialGameBoard = GameBoard(List.empty[Card], List.empty[Card]).shuffleDeck()

    val playerHands = List.fill(numberPlayers)(PlayerHand(List.empty[Card]))
    var gameState = GameState(playerHands, initialGameBoard, 0, initialGameBoard.drawPile)
    
    gameState = gameState.dealInitialCards(cardsPerPlayer)

    UnoTui.startGame(gameState)
  }
}
