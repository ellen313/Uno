package de.htwg.se.uno.model.gameComponent.mock

import de.htwg.se.uno.model.cardComponent.{Card, WildCard}
import de.htwg.se.uno.model.playerComponent.PlayerHand
import de.htwg.se.uno.model.gameComponent.base.GameState
import de.htwg.se.uno.model.gameComponent.{GameStateInterface, InputResult, Success}

case class GameStateMock( override val players: List[PlayerHand] = List.fill(2)(PlayerHand(List())),
                          override val currentPlayerIndex: Int = 0,
                          override val allCards: List[Card] = List.empty,
                          override val isReversed: Boolean = false,
                          override val discardPile: List[Card] = List.empty,
                          override val drawPile: List[Card] = List.empty,
                          override val selectedColor: Option[String] = None
                        ) extends GameStateInterface {

  override def nextPlayer(): GameStateMock = {
    this.copy(currentPlayerIndex = (currentPlayerIndex + 1) % players.length)
  }

  override def checkForWinner(): Option[Int] = None

  override def playCard(card: Card, chosenColor: Option[String] = None): GameStateMock = {
    this.copy(discardPile = card :: discardPile)
  }

  override def isValidPlay(card: Card, topCard: Option[Card], selectedColor: Option[String]): Boolean = true

  override def drawCardAndReturnDrawn(): (GameStateMock, Card) = {
    val dummyCard = WildCard("wild")
    (this.copy(), dummyCard)
  }

  override def inputHandler(input: String): InputResult = {
    Success(this)
  }

  override def notifyObservers(): Unit = {
  }

  override def dealInitialCards(cardsPerPlayer: Int): GameStateInterface = ???

  override def playerSaysUno(playerIndex: Int): GameStateInterface = ???

  override def drawCard(playerHand: PlayerHand, drawPile: List[Card], discardPile: List[Card]): (Card, PlayerHand, List[Card], List[Card]) = ???

  override def handleDrawCards(count: Int): GameStateInterface = ???

  override def setSelectedColor(color: String): GameStateInterface = ???
}
