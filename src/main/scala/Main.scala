object Main {
  case class Card(value: Int, color: String, number: Int){
    def isSet: Boolean = value != 0
  }
  case class PlayerHand(cards: List[Card])
  case class GameBoard(playerStacks: List[List[Card]], centerStack: List[Card], drawPile: List[Card], discardPile: List[Card])

  case class GameState(players: List[PlayerHand], gameBoard: GameBoard, currentPlayerIndex: Int)

  /**Function to print the game board*/

  println("Game condition")
  def printGameBoard(gameState: GameState): Unit = {
    println("Player Hand:")
    for (card <- gameState.players.head.cards) {
      // print color and number
      println(card.color + "-" + card.number)
    }

    println("\nPlayer Stacks:")
    for (stack <- gameState.gameBoard.playerStacks) {
      // run through the cars in each playerstack
      for (card <- stack) {
        // print color and number 
        println(card.color + "-" + card.number)
      }
      //seperate player stacks
      println("---")
    }

    println("\nCenter Stack:")
    for (card <- gameState.gameBoard.centerStack) {
      println(card.color + "-" + card.number)
    }
    println("\nDraw Pile:")
    for (card <- gameState.gameBoard.drawPile) {
      println(card.color + "-" + card.number)
    }
    println("\nDiscard Pile:")
    for (card <- gameState.gameBoard.discardPile) {
      println(card.color + "-" + card.number)
    }
  }

  // main method
  def main(args: Array[String]): Unit = {
    val playerHand = PlayerHand(List(Card(1, "red", 2), Card(2, "blue", 3)))
    val playerStacks = List(List(Card(3, "green", 1)), List(Card(4, "yellow", 4)))
    val centerStack = List(Card(5, "red", 3))
    val drawPile = List(Card(6, "green", 2))
    val discardPile = List(Card(7, "yellow", 1))

    val gameBoard = GameBoard(playerStacks, centerStack, drawPile, discardPile)
    val gameState = GameState(List(playerHand), gameBoard, currentPlayerIndex = 0)

    printGameBoard(gameState)
  }
}
