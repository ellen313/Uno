package de.htwg.se.uno.model.gameComponent

import de.htwg.se.uno.model.cardComponent.Card
import de.htwg.se.uno.model.playerComponent.PlayerHand
import de.htwg.se.uno.util.Observable
import de.htwg.se.uno.model.gameComponent.InputResult

trait GameStateInterface extends Observable {
  def players: List[PlayerHand]
  def currentPlayerIndex: Int
  def allCards: List[Card]
  def isReversed: Boolean
  def discardPile: List[Card]
  def drawPile: List[Card]
  def selectedColor: Option[String]

  def nextPlayer(): GameStateInterface
  def dealInitialCards(cardsPerPlayer: Int): GameStateInterface
  def checkForWinner(): Option[Int]
  def playerSaysUno(playerIndex: Int): GameStateInterface
  def drawCard(playerHand: PlayerHand, drawPile: List[Card], discardPile: List[Card]):
  (Card, PlayerHand, List[Card], List[Card])
  def playCard(card: Card, chosenColor: Option[String] = None): GameStateInterface
  def handleDrawCards(count: Int): GameStateInterface
  def isValidPlay(card: Card, topCard: Option[Card], selectedColor: Option[String] = None): Boolean
  def drawCardAndReturnDrawn(): (GameStateInterface, Card)
  def setSelectedColor(color: String): GameStateInterface
  def inputHandler(input: String): InputResult
}
