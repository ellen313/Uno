package de.htwg.se.uno.model.state

import de.htwg.se.uno.model.*

case class PlayCardState(context: UnoStates, card: Card) extends GamePhase {

  override def playCard(): GamePhase = {
    context.gameState = context.gameState.playCard(card)
    context.setState(PlayerTurnState(context))

    val finalGameState = card match {
      //------------ skip ------------
      case ActionCard(_, "skip") =>
        context.gameState = context.gameState.nextPlayer().nextPlayer()
        PlayerTurnState(context)

      //------------ reverse ------------
      case ActionCard(_, "reverse") =>
        context.gameState = context.gameState.copy(isReversed = !context.gameState.isReversed)
        PlayerTurnState(context)

      //------------ draw two ------------
      case ActionCard(_, "draw two") =>
        context.gameState = handlePenaltyCards(context.gameState, 2).nextPlayer()
        PlayerTurnState(context)

      //------------ wild draw four ------------
      case WildCard("wild draw four") =>
        context.gameState = handlePenaltyCards(context.gameState, 4).nextPlayer()
        PlayerTurnState(context)

      //------------ wild card ------------    
      case WildCard("wild") =>
        context.gameState = context.gameState.nextPlayer()
        PlayerTurnState(context)    

      //------------ default ------------
      case _ =>
        context.gameState = context.gameState.nextPlayer()
        PlayerTurnState(context)
    }

    checkUnoStatus(context)
    finalGameState
  }

  private def handlePenaltyCards(state: GameState, count: Int): GameState = {
    val nextPlayerIdx = if (state.isReversed) {
      (state.currentPlayerIndex - 1 + state.players.length) % state.players.length
    } else {
      (state.currentPlayerIndex + 1) % state.players.length
    }

    val (updatedHand, updatedDrawPile, _) =
      (1 to count).foldLeft((state.players(nextPlayerIdx), state.drawPile, state.discardPile)) {
        case ((hand, draw, _), _) =>
          val (_, newHand, newDraw, _) = state.drawCard(hand, draw, Nil)
          (newHand, newDraw, Nil)
      }

    state.copy(
      players = state.players.updated(nextPlayerIdx, updatedHand),
      drawPile = updatedDrawPile
    )
  }

  private def checkUnoStatus(context: UnoStates): Unit = {
    val currentPlayer = context.gameState.currentPlayerIndex
    if (context.gameState.players(currentPlayer).cards.size == 1) {
      context.setState(UnoCalledState(context))
    }
  }

  override def nextPlayer(): GamePhase = PlayerTurnState(context)

  override def dealInitialCards(): GamePhase = {
    println("Cannot deal cards during PlayCardState")
    this
  }

  override def checkForWinner(): GamePhase = GameOverState(context)

  override def playerSaysUno(): GamePhase = UnoCalledState(context)

  override def drawCard(): GamePhase = {
    println("Cannot draw a card during PlayCardState")
    this
  }

  override def isValidPlay: Boolean = {
    val topCard = context.gameState.discardPile.lastOption
    context.gameState.isValidPlay(card, topCard, context.gameState.selectedColor)
  }
}
