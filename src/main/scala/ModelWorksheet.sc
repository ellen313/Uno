case class Card(value: Int, color: String, number: Int) {
  def isSet:Boolean = value != 0
}
val card1 = Card(1, red, 2)
card1.isSet

case class PlayerHand(cards: List[Card]) { //A player's hand can be displayed as a list of cards

}

case class GameBoard(playerStacks: List[List[Card]], centerStack: List[Card], drawPile: List[Card], discardPile: List[Card])

case class GameState(players: List[PlayerHand], gameBoard: GameBoard, currentPlayerIndex: Int)

