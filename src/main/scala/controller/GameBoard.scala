package controller

import model.*

import scala.util.Random

case class GameBoard(var game: GameState, drawPile: List[Card], discardPile: List[Card]) {
  
  def createDeckWithAllCards(): List[Card] = {
    val numberCards = for {
      // 1x0 and 2x1-9 for each color
      number <- 0 to 9
      color <- List("yellow", "red", "blue", "green")
    } yield {
      if (number == 0) {
        List(NumberCard(color, number))
      } else {
        List(
          NumberCard(color, number), //1. card
          NumberCard(color, number) //2. card
        )
      }
    }
    val allNumberCards = numberCards.flatten.toList

    val actionCards = List("draw two", "skip", "reverse").flatMap { action => //24 cards
      List(
        ActionCard("red", action),
        ActionCard("red", action),
        ActionCard("blue", action),
        ActionCard("blue", action),
        ActionCard("green", action),
        ActionCard("green", action),
        ActionCard("yellow", action),
        ActionCard("yellow", action)
      )
    }
    val wildCards = List.fill(4)(WildCard("wild")) ++ List.fill(4)(WildCard("wild draw four")) //8 cards
    allNumberCards ++ actionCards ++ wildCards // 108 cards
  }

  def shuffleDeck(): GameBoard = {
    val allCards = createDeckWithAllCards()
    val shuffledCards = Random.shuffle(allCards)

    val discardPile = shuffledCards.headOption match {
      case Some(card) => List(card)
      case None => List.empty[Card]
    }
    val drawPile = if (shuffledCards.isEmpty) List.empty[Card] else shuffledCards.tail

    GameBoard(game, drawPile, discardPile)
  }
  
}
