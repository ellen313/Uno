package de.htwg.se.uno.controller

import de.htwg.se.uno.model.*
import scala.util.Random
import de.htwg.se.uno.util.{Observable, Observer}
import de.htwg.se.uno.controller.command.*

object GameBoard extends Observable {

  var gameState: GameState = _
  var drawPile: List[Card] = Nil
  var discardPile: List[Card] = Nil

  // Methods for command pattern
  def state: GameState = gameState

  def players: List[PlayerHand] = gameState.players

  def currentPlayerIndex: Int = gameState.currentPlayerIndex

  def selectedColor: Option[String] = gameState.selectedColor

  def setSelectedColor(color: String): Unit = {
    gameState = gameState.copy(selectedColor = Some(color))
  }

  def init_state(state: GameState): Unit = {
    this.gameState = state
    shuffleDeck()
  }

  def setGameState(state: GameState): Unit = {
    gameState = state
    notifyObservers()
  }

  def init_board(players: List[PlayerHand]): Unit = {
    gameState = GameState(
      players = players,
      currentPlayerIndex = 0,
      allCards = List.empty[Card],
      isReversed = false,
      drawPile = List.empty[Card],
      discardPile = List.empty[Card],
      selectedColor = None
    )
    shuffleDeck()
  }

  def createDeckWithAllCards(): List[Card] = {
    val numberCards = for {
      // 1x0 and 2x1-9 for each color
      number <- 0 to 9
      color <- List("yellow", "red", "blue", "green")
      count = if (number == 0) 1 else 2
    } yield Card("number")

    val actionCards = for {
      _ <- 1 to 2
      action <- Card.actions
      color <- Card.colors
    } yield Card("action")
      val wildCards = List.fill(4)(WildCard("wild")) ++ List.fill(4)(WildCard("wild draw four"))

      numberCards.toList ++ actionCards.toList ++ wildCards
  }

  def shuffleDeck(): Unit = {
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

  override def addObserver(observer: Observer): Unit = {
     super.addObserver(observer)
  }

  def isValidPlay(card: Card, topCard: Card, selectedColor: Option[String]): Boolean = {
    gameState.isValidPlay(card, Some(topCard), selectedColor)
  }

  def reset(): Unit = {
    gameState = null
    drawPile = Nil
    discardPile = Nil
  }
  
}
