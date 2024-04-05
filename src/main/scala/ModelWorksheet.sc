object Main {
  case class Card(value: Int, color: String, number: Int){
    def isSet: Boolean = value != 0
  }
  case class PlayerHand(cards: List[Card])
  case class GameBoard(playerStacks: List[List[Card]], centerStack: List[Card], drawPile: List[Card], discardPile: List[Card])

  case class GameState(players: List[PlayerHand], gameBoard: GameBoard, currentPlayerIndex: Int)

  // Funktion zur Ausgabe des Spielbretts als Strings
  def printGameBoard(gameState: GameState): Unit = {
    println("Player Hand:")
    gameState.players.head.cards.foreach(card => println(s"${card.color}-${card.number}"))

    println("\nPlayer Stacks:")
    gameState.gameBoard.playerStacks.foreach(stack => {
      stack.foreach(card => println(s"${card.color}-${card.number}"))
      println("---")
    })

    println("\nCenter Stack:")
    gameState.gameBoard.centerStack.foreach(card => println(s"${card.color}-${card.number}"))

    println("\nDraw Pile:")
    gameState.gameBoard.drawPile.foreach(card => println(s"${card.color}-${card.number}"))

    println("\nDiscard Pile:")
    gameState.gameBoard.discardPile.foreach(card => println(s"${card.color}-${card.number}"))
  }

  // Main-Methode
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

