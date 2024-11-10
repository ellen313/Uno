
case class PlayerHand(cards: List[Card]) {

  //add a card to players hand
  def addCard(card: Card): PlayerHand = {
    copy(cards = card :: cards) //add card to the head of list cards
  }
  def containsCard(card: Card): Boolean = cards.contains(card)
  
  //remove a card from players hand
  def removeCard(card: Card): PlayerHand = {
    copy(cards = cards.filter(c => c != card))
  }

  //check if player can say 'Uno'
  def hasUno: Boolean = cards.length == 1

  //if hand is empty
  def isEmpty: Boolean = cards.isEmpty

  //display cards on players hand
  def displayHand(): Unit = {
    cards.foreach {
      case NumberCard(color, number) =>
        println(s"$color-$number")
      case ActionCard(color, actionType) =>
        println(s"$color-$actionType")
      case WildCard(actionType) =>
        println(s"$actionType")
    }
  }

  def sortHand(): PlayerHand = {
    val sortedCards = cards.sortBy {
      case NumberCard(color, number) => (1, color, number)
      case ActionCard(color, _) => (2, color, Int.MaxValue)
      case WildCard(_) => (3, "", Int.MaxValue)
    }
    copy(cards = sortedCards)
  }
}
