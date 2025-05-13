package de.htwg.se.uno.controller

import de.htwg.se.uno.model.*
import scala.util.Random
import de.htwg.se.uno.util.{Observable, Observer}
import de.htwg.se.uno.controller.command.*

object GameBoard extends Observable {

  var gameState: GameState = _
  var drawPile: List[Card] = Nil
  var discardPile: List[Card] = Nil
  
  def state: GameState = gameState

  def init(state: GameState): Unit = {
    this.gameState = state
    shuffleDeck()
  }

  def createDeckWithAllCards(): List[Card] = {
    val numberCards = for {
      // 1x0 and 2x1-9 for each color
      number <- 0 to 9
      color <- Card.colors
      count = if (number == 0) 1 else 2
      _ <- 1 to count
    } yield Card("number")

    val actionCards = for {
      _ <- 1 to 2
      action <- Card.actions
      color <- Card.colors
    } yield Card("action")
    
    val wildCards = List.fill(4)(WildCard("wild")) ++ List.fill(4)(WildCard("wild draw four")) //8 cards
    
    numberCards.toList ++ actionCards.toList ++ wildCards // 108 cards
  }

  def shuffleDeck(): GameBoard = {
    val shuffled = Random.shuffle(createDeckWithAllCards())
    discardPile = shuffled.headOption.toList
    drawPile = shuffled.drop(1)
  }

  def executeCommand(command: Command): Unit = {
    command.execute()
    gameState.notifyObservers()
  }

  def checkForWinner(): Option[Int] = {
    gameState.players.zipWithIndex.find(_._1.cards.isEmpty).map(_._2)
  }

  def addObserver(observer: Observer): Unit = {
    super.addObserver(observer)
  }

  def isValidPlay(card: Card, topCard: Card, chosenColor: Option[String]): Boolean = {
    gameState.isValidPlay(card, Some(topCard), chosenColor)
  }

  def reset(): Unit = {
    gameState = null
    drawPile = Nil
    discardPile = Nil
  }
}
