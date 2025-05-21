package de.htwg.se.uno.model

import scala.annotation.targetName

case class PlayerHand(cards: List[Card], var hasSaidUno: Boolean = false) {
  
  @targetName("addCard")
    def +(card: Card): PlayerHand = {
    copy(cards = card :: cards)
  }
  def containsCard(card: Card): Boolean = cards.contains(card)
  
  def removeCard(card: Card): PlayerHand = {
    val (before, after) = cards.span(_ != card)
    copy(cards = before ++ after.drop(1), hasSaidUno = false)
  }
  
  def hasUno: Boolean = cards.length == 1

  def sayUno(): PlayerHand = {
    this.copy(hasSaidUno = true)
  }

  def resetUnoStatus(): PlayerHand = {
    copy(hasSaidUno = false)
  }
  
  def isEmpty: Boolean = cards.isEmpty
  
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
