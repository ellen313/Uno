
  case class Card(color: String, number: Int){
    def isSet: Boolean = value != 0
  }
  val card1=Card(1,"red",1)
  card1.isSet
  card1.color


  case class PlayerHand(cards: List[Card])

  val hand1=PlayerHand(List(card1))
  case class GameBoard(playerStacks: List[List[Card]], centerStack: List[Card], drawPile: List[Card], discardPile: List[Card])

  case class GameState(players: List[PlayerHand], gameBoard: GameBoard, currentPlayerIndex: Int)


  def main(args: Array[String]): Unit = {
    val playerHand = PlayerHand(List(Card(createRandomCard()), Card(createRandomCard())))
    val playerStacks = List(List(Card(createRandomCard())), List(Card(createRandomCard())))
    val centerStack = List(Card(createRandomCard()))
    val drawPile = List(Card(createRandomCard()))
    val discardPile = List(Card(createRandomCard()))

    val gameBoard = GameBoard(playerStacks, centerStack, drawPile, discardPile)
    val gameState = GameState(List(playerHand), gameBoard, currentPlayerIndex = 0)

  }


