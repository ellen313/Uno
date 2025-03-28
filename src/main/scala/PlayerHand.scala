import scala.annotation.targetName

case class PlayerHand(cards: List[Card]) {

  //def addCard(card: Card): PlayerHand = {
    //copy(cards = card :: cards) //add card to the head of list cards
  //}

  //create operator (instead of addCard method) -> can be used as an operator e.g. 'hand + Card("7", "red")'
  @targetName("addCard") //JVM does not support operators as method names, needs operation name and targetName annotation
  def +(card: Card): PlayerHand = {
    copy(cards = card :: cards)
  }

  def removeCard(card: Card): PlayerHand = {
    copy(cards = cards.filter(c => c != card))
  }

  //check if player can say 'Uno'
  def hasUno: Boolean = cards.length == 1

  def isEmpty: Boolean = cards.isEmpty

  //display cards on players hand
  def displayHand(): Unit = {
    cards.foreach {
      case NumberCard(color, number) =>
        println(s"$color-$number") // instead of (color + "-" + number)
      case ActionCard(color, actionType) =>
        println(s"$color-$actionType")
      case WildCard(actionType) =>
        println(s"$actionType")
    }
  }

  def sortHand(): PlayerHand = {
    val sortedCards = cards.sortBy {
      case NumberCard(color, number) => (1, color, number)
      case ActionCard(color, _) => (2, color, Int.MaxValue) // only need first value with pattern matching
      case WildCard(_) => (3, "", Int.MaxValue) // MaxValue is place holder because we need tuple
    }
    copy(cards = sortedCards)
  }
}
