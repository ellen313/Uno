package de.htwg.se.uno.controller

import de.htwg.se.uno.model.*
import scala.util.Random
import de.htwg.se.uno.util.{Observable, Observer}
import de.htwg.se.uno.controller.command.*

object GameBoard extends Observable {

  private var _gameState: Option[GameState] = None

  val fullDeck: List[Card] = createDeckWithAllCards()

  val (discardPile, drawPile) = {
    val shuffled = Random.shuffle(fullDeck)
    (shuffled.headOption.toList, shuffled.tail)
  }

  def gameState: GameState = _gameState.getOrElse(
    throw new IllegalStateException("GameState not initialized")
    //Try
  )

  def updateState(newState: GameState): Unit = {
    _gameState = Some(newState)
  }

  def initGame(state: GameState): Unit = {
    val (discard, draw) = shuffleDeck()
    val initializedState = state.copy(
      drawPile = draw,
      discardPile = discard,
      allCards = discard ++ draw
    )
    updateState(initializedState)
  }

  // Methods for command pattern
//  def state: GameState = gameState
//
//  def players: List[PlayerHand] = gameState.players
//
//  def currentPlayerIndex: Int = gameState.currentPlayerIndex
//
//  def selectedColor: Option[String] = gameState.selectedColor
//
//  def setSelectedColor(color: String): Unit = {
//    updateState(gameState.copy(selectedColor = Some(color)))
//  }

  def createDeckWithAllCards(): List[Card] = {
    val numberCards = for {
      // 1x0 and 2x1-9 for each color
      number <- 0 to 9
      color <- Card.colors
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

  def shuffleDeck(): (List[Card], List[Card]) = {
    val shuffled = Random.shuffle(createDeckWithAllCards())
    (shuffled.headOption.toList, shuffled.tail)
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
    _gameState = None
  }
  
}
