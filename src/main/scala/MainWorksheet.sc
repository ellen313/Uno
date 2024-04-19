case class Card(value: Int, color: String, number: Int) {
  def isSet: Boolean = value != 0
}
case class PlayerHand(cards: List[Card])
case class GameBoard(
    playerStacks: List[List[Card]],
    centerStack: List[Card],
    drawPile: List[Card],
    discardPile: List[Card]
)

case class GameState(
    players: List[PlayerHand],
    gameBoard: GameBoard,
    currentPlayerIndex: Int
)

def main(args: Array[String]): Unit = {
  val playerHand = PlayerHand(List(Card(1, "red", 2), Card(2, "blue", 3)))
  val playerStacks = List(List(Card(3, "green", 1)), List(Card(4, "yellow", 4)))
  val centerStack = List(Card(5, "red", 3))
  val drawPile = List(Card(6, "green", 2))
  val discardPile = List(Card(7, "yellow", 1))

  val gameBoard = GameBoard(playerStacks, centerStack, drawPile, discardPile)
  val gameState = GameState(List(playerHand), gameBoard, currentPlayerIndex = 0)

}
