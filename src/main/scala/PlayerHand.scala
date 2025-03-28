import scala.annotation.targetName

case class PlayerHand(cards: List[Card]) {

  //def addCard(card: Card): PlayerHand = {
  //copy(cards = card :: cards) //add card to the head of list cards
  //}

  //create operator (instead of addCard method)
  @targetName("addCard") //JVM does not support operators as method names, needs operation name and targetName annotation
  def +(card: Card): PlayerHand = {
    copy(cards = card :: cards)
  }
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
